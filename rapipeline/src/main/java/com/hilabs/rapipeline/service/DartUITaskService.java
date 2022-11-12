package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.rapipeline.model.DartStatusCheckResponse;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.service.FileSystemUtilService.downloadUsingNIO;
import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.hasIntersection;
import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.isSubset;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.*;

@Service
@Slf4j
public class DartUITaskService {
    @Value("${dartUIHost}")
    private String dartUIHost;

    @Value("${dartUIToken}")
    private String dartUIToken;
    private static Gson gson = new Gson();
    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private RASheetDetailsService raSheetDetailsService;

    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;

    @Autowired
    private RAFileStatusUpdatingService raFileStatusUpdatingService;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    @Autowired
    private RestTemplate restTemplate;

    public static ConcurrentHashMap<Long, Boolean> dartUITaskRunningMap = new ConcurrentHashMap<>();

    public List<RASheetDetails> getEligibleRASheetDetailsListAndUpdate(int count) {
        List<Integer> dartUIValidationInProgressSheetStatusCodeList = Collections.singletonList(dartUIValidationInProgressSheetStatusCode);
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsBasedOnSheetStatusCodesForUpdate(dartUIValidationInProgressSheetStatusCodeList,
                count);
        List<Long> raSheetDetailsIds = raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        //TODO demo
        raSheetDetailsRepository.updateRASheetDetailsStatusByIds(raSheetDetailsIds, dartUIFeedbackInQueueSheetStatusCode,
                "SYSTEM", new Date());
        return raSheetDetailsList;
    }

    public void invokePythonProcessForDartUITask(RASheetDetails raSheetDetails) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getDartUIWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(),"--envConfigs", appPropertiesConfig.getEnvConfigs(),
                    "--sheetDetailsId",  "" + raSheetDetails.getId());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }

    public static String getUrl(String host, String path) {
        if (host.endsWith("/")) {
            host += "/";
        }
        return String.format("%s%s", host, path);
    }

    //TODO demo
    public DartStatusCheckResponse checkDartUIStatusOfSheet(String validationFileId) {
        try {
            String url = getUrl(dartUIHost, String.format("dart-core-service/file-validation/v1/file-status/%s", validationFileId));
            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization", "Bearer " + dartUIToken); //accessToken can be the secret key you generate.
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestJson = "";
            HttpEntity<String> entity = new HttpEntity <> (requestJson, headers);
            ResponseEntity<DartStatusCheckResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, DartStatusCheckResponse.class);
            return response.getBody();
        } catch (Exception ex) {
            log.error("Error in checkDartUIStatusOfSheet for validationFileId {} ex {} stackTrace {}",
                    validationFileId, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }

    public String downloadDartUIResponseFile(String validationFileId, String folderPath, String fileType) throws IOException  {
        try {
            String urlString = getUrl(dartUIHost, String.format("dart-core-service/file-validation/v1/file-download/%s?type=%s", validationFileId, fileType));
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setRequestProperty("Authorization", "Bearer " + dartUIToken);
            String fieldValue = con.getHeaderField("Content-Disposition");
            if (fieldValue == null || ! fieldValue.contains("filename=\"")) {
                log.error("No filename for validationFileId {} fileType {} urlString {}", validationFileId, fileType, urlString);
                return null;
            }
            String filename = fieldValue.substring(fieldValue.indexOf("filename=\"") + 10, fieldValue.length() - 1);
            File download = new File(folderPath, filename);
            ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
            try (FileOutputStream fos = new FileOutputStream(download)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            return filename;
        } catch (Exception ex) {
            log.error("Error in downloadDartUIResponseFile for validationFileId {} folderPath {} fileType {} ex {} stackTrace {}",
                    validationFileId, folderPath, fileType, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }

    public boolean consolidateDartUIValidation(Long raFileDetailsId) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
        log.info("consolidateDartUIValidation for raFileDetailsId {} raSheetDetailsList {}", raFileDetailsId,
                new Gson().toJson(raSheetDetailsList.stream().map(p -> p.getId())));
        List<Integer> sheetCodes = raSheetDetailsList.stream().map(s -> s.getStatusCode()).collect(Collectors.toList());
        if (sheetCodes.stream().anyMatch(Objects::isNull)) {
            log.error("One of the status codes is null for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return false;
        }
        if (sheetCodes.size() == 0) {
            log.error("Zero status codes for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return false;
        }
        //153 - ISF Conversion Failed
        //111 - Roster Sheet Processing not Required
        //119 - Roster Sheet Need to be Processed Manually
        //131 - Post Column Mapping Normalization processing Not required
        //139 - Post Column Mapping Normalization Manual action
        //155 - ISF Conversion Completed
        //145 - Post Column Mapping Normalization completed
        //35 - Roster ISF Generation Completed
        //33 - Roster ISF Generation Failed
        if (hasIntersection(Arrays.asList(173), sheetCodes)) {
            //TODO demo
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 33);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139, 179))) {
            //TODO demo - change file status code
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 35);
            return false;
        }
        return true;
    }
}

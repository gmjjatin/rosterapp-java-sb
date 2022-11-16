package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.model.FileMetaDataTableStatus.FAILED;
import static com.hilabs.rapipeline.service.FileSystemUtilService.getListOfFilesInFolder;
import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.hasIntersection;
import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.isSubset;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.*;

@Service
@Slf4j
public class SpsTaskService {
    @Value("${spsSrcFolder}")
    private String spsSrcFolder;

    @Value("${spsDestFolder}")
    private String spsDestFolder;
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
    private FileSystemUtilService fileSystemUtilService;

    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> spsTaskRunningMap = new ConcurrentHashMap<>();

    public List<RASheetDetails> getEligibleRASheetDetailsListAndUpdate(int count) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsBasedFileStatusAndSheetStatusCodesForUpdate(Arrays.asList(dartUIValidationCompleteFileStatusCode),
                Arrays.asList(readyForSpsSheetStatusCode), Arrays.asList(0, 1), count);
        List<Long> raSheetDetailsIds = raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        raSheetDetailsRepository.updateRASheetDetailsStatusByIds(raSheetDetailsIds,
                spsInQueueSheetStatusCode, "SYSTEM", new Date());
        raSheetDetailsList.stream().forEach(p -> p.setStatusCode(spsInQueueSheetStatusCode));
        return raSheetDetailsList;
    }

    public void invokePythonProcessForSpsTask(RASheetDetails raSheetDetails) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getSpsWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(),"--envConfigs", appPropertiesConfig.getEnvConfigs(),
                    "--sheetDetailsId",  "" + raSheetDetails.getId());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }

    public static String removeFileExtensionFromExcelFile(String fileName) {
        if (fileName == null) {
            return null;
        }
        fileName = fileName.replaceAll(".xlsx", "");
        fileName = fileName.replaceAll(".xlsm", "");
        fileName = fileName.replaceAll(".xls", "");
        return fileName;
    }

    public Optional<String> checkAndGetSpsResponseFilePathIfExists(RASheetDetails raSheetDetails) {
        String fileNameWithoutExt = removeFileExtensionFromExcelFile(raSheetDetails.getOutFileName());
        log.info("Searching in spsSrcFolder {} fileNameWithoutExt {}", spsSrcFolder, fileNameWithoutExt);
        String[] fileList = getListOfFilesInFolder(spsSrcFolder, fileNameWithoutExt, "");
        if (fileList.length == 0) {
            return Optional.empty();
        }
        String fileName = fileList[0];
        return Optional.of(new File(spsSrcFolder, fileName).getPath());
    }

    public static String getFileNameFromPath(String filePath) {
        String[] parts = filePath.split("/");
        return parts[parts.length - 1];
    }

    public void copySpsResponseFileToDestination(String filePath) throws IOException  {
        String fileName = getFileNameFromPath(filePath);
        File file = new File(filePath);
        InputStream dataStream = Files.newInputStream(file.toPath());
        FileUtils.copyInputStreamToFile(dataStream, new File(spsDestFolder, fileName));
    }

    public void consolidateSpsValidation(Long raFileDetailsId) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
        log.info("consolidateDartUIValidation for raFileDetailsId {} raSheetDetailsList {}", raFileDetailsId,
                new Gson().toJson(raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList())));
        List<Integer> sheetCodes = raSheetDetailsList.stream().map(s -> s.getStatusCode()).collect(Collectors.toList());
        if (sheetCodes.stream().anyMatch(Objects::isNull)) {
            log.error("One of the status codes is null for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return;
        }
        if (sheetCodes.size() == 0) {
            log.error("Zero status codes for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return;
        }

        //111 - Roster Sheet Processing not Required
        //119 - Roster Sheet Need to be Processed Manually
        //131 - Post Column Mapping Normalization processing Not required
        //139 - Post Column Mapping Normalization Manual action
        //155 - ISF Conversion Completed
        //145 - Post Column Mapping Normalization completed

        //179 - Dart UI validation completed

        //53 - Failed DART UI validation (All file)
        //55 - All sheeets pass dart ui validation
        //57 - partially completed for dart ui validation.
        int spsSuccessStatusCode = 189;
        int spsFailedStatusCode = 188;
        if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139, spsSuccessStatusCode))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 65);
            return;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139, spsSuccessStatusCode, spsFailedStatusCode))) {
            if (hasIntersection(sheetCodes, Collections.singletonList(spsSuccessStatusCode))) {
                raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 67);
                return;
            } else {
                try {
                    raFileMetaDataDetailsService.updatePlmStatusForFileDetailsId(raFileDetailsId, FAILED);
                } catch (Exception ex) {
                    log.error("Error in updatePlmStatusForFileDetailsId with failed status for raFileDetailsId {}", raFileDetailsId);
                }
                raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 63);
                return;
            }
        }
    }
}

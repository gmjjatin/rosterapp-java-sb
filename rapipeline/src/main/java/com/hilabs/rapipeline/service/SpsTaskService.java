package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.service.FileSystemUtilService.getListOfFilesInFolder;
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
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> spsTaskRunningMap = new ConcurrentHashMap<>();

    public List<RASheetDetails> getEligibleRASheetDetailsListAndUpdate(int count) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsBasedFileStatusAndSheetStatusCodesForUpdate(Arrays.asList(dartUIValidationCompleteFileStatusCode),
                Arrays.asList(readyForSpsSheetStatusCode), Arrays.asList(0, 1), count);
        List<Long> raSheetDetailsIds = raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        raSheetDetailsRepository.updateRASheetDetailsStatusByIds(raSheetDetailsIds,
                spsInQueueSheetStatusCode, "SYSTEM", new Date());
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

    public Optional<String> checkAndGetSpsResponseFilePathIfExists(RASheetDetails raSheetDetails) {
        String[] fileList = getListOfFilesInFolder(spsSrcFolder, raSheetDetails.getOutFileName(), "");
        if (fileList.length == 0) {
            return Optional.empty();
        }
        return Optional.of(fileList[0]);
    }

    public void copySpsResponseFileToDestination(String filePath) {
        String[] parts = filePath.split("/");
        String fileName = parts[parts.length - 1];
        fileSystemUtilService.copyFileToDest(filePath, new File(spsDestFolder, fileName).getPath());
    }
}

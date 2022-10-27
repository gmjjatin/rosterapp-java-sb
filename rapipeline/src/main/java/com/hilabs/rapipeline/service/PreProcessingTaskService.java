package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.preProcessingInQueueStatus;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.preProcessingStatusCodes;

@Service
@Slf4j
public class PreProcessingTaskService {
    private static Gson gson = new Gson();
    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> preProcessingRunningMap = new ConcurrentHashMap<>();

//    public boolean shouldRun(Long raFileDetailsId) {
//        if (preProcessingRunningMap.containsKey(raFileDetailsId)) {
//            //Just logging
//            log.warn("PreProcessingTask task in progress for raFileDetailsId {}", raFileDetailsId);
//        }
//        return isFileIdEligibleForPreProcessingTask(raFileDetailsId);
//    }

//    public boolean isFileIdEligibleForPreProcessingTask(Long raFileDetailsId) {
//        Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByRAFileDetailsId(raFileDetailsId);
//        if (!optionalRAFileDetails.isPresent()) {
//            return false;
//        }
//        RAFileDetails raFileDetails = optionalRAFileDetails.get();
//        if (raFileDetails.getStatusCode() == null) {
//            return false;
//        }
//        if (!raFileDetails.getStatusCode().equals(preProcessingInQueueStatus)) {
//            return false;
//        }
//        return true;
//    }


    @Transactional
    public List<RAFileDetails> getEligibleRAFileDetailsListAndUpdate(int count) {
        List<RAFileDetails> raFileDetailsList = raFileDetailsRepository.findFileDetailsByStatusCodesForUpdate(preProcessingStatusCodes, count);
        List<Long> raFileDetailsIds = raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        raFileDetailsRepository.updateRAFileDetailsStatusByIds(raFileDetailsIds, preProcessingInQueueStatus);
        return raFileDetailsList;
    }

    public void invokePythonProcessForPreProcessingTask(Long raFileDetailsId) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            //TODO hardcode for now
            File file = new File(appPropertiesConfig.getPreProcessingWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(), "--envConfigs", appPropertiesConfig.getEnvConfigs(),
                    "--fileDetailsId",  "" + raFileDetailsId);
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

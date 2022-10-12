package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.rapipeline.service.PythonInvocationService;
import com.hilabs.rapipeline.service.RAFileDetailsService;
import com.hilabs.roster.entity.RAFileDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.preProcessingStatusCodes;

@Service
@Slf4j
public class PreProcessingTaskService {
    private static Gson gson = new Gson();
    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> preProcessingRunningMap = new ConcurrentHashMap<>();

    public boolean shouldRun(Long raFileDetailsId) {
        if (preProcessingRunningMap.containsKey(raFileDetailsId)) {
            log.warn("PreProcessingTask task in progress for raFileDetailsId {}", raFileDetailsId);
            return false;
        }
        return isFileIdEligibleForPreProcessingTask(raFileDetailsId);
    }

    public boolean isFileIdEligibleForPreProcessingTask(Long raFileDetailsId) {
        Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByRAFileDetailsId(raFileDetailsId);
        if (!optionalRAFileDetails.isPresent()) {
            return false;
        }
        RAFileDetails raFileDetails = optionalRAFileDetails.get();
        if (raFileDetails.getStatusCode() == null) {
            return false;
        }
        if (!preProcessingStatusCodes.stream().anyMatch(p -> raFileDetails.getStatusCode().equals(p))) {
            return false;
        }
        return true;
    }


    public List<RAFileDetails> getEligibleRAFileDetailsList(int count) {
        return raFileDetailsService.findFileDetailsByStatusCodes(preProcessingStatusCodes, count, 0);
    }

    public void invokePythonProcessForPreProcessingTask(Long raFileDetailsId) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            //TODO hardcode for now
            File file = new File(appPropertiesConfig.getPreProcessingWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(), "--fileId",  "" + raFileDetailsId, "--rootPath",
                    appPropertiesConfig.getRootPath());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

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

import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.dartStatusCodes;

@Service
@Slf4j
public class DartTaskService {
    private static Gson gson = new Gson();
    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> dartTaskRunningMap = new ConcurrentHashMap<>();

    public boolean shouldRun(Long raFileDetailsId) {
        if (dartTaskRunningMap.containsKey(raFileDetailsId)) {
            log.warn("DartTask task in progress for raFileDetailsId {}", raFileDetailsId);
            return false;
        }
        return isFileIdEligibleForDartTask(raFileDetailsId);
    }

    public List<RAFileDetails> getEligibleRAFileDetailsList(int count) {
        return raFileDetailsService.findFileDetailsByStatusCodes(dartStatusCodes, count, 0);
    }

    public boolean isFileIdEligibleForDartTask(Long raFileDetailsId) {
        Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByRAFileDetailsId(raFileDetailsId);
        if (!optionalRAFileDetails.isPresent()) {
            return false;
        }
        RAFileDetails raFileDetails = optionalRAFileDetails.get();
        if (raFileDetails.getStatusCode() == null) {
            return false;
        }
        if (!dartStatusCodes.stream().anyMatch(p -> raFileDetails.getStatusCode().equals(p))) {
            return false;
        }
        return raFileDetails.getManualActionRequired() == null || raFileDetails.getManualActionRequired() == 0;
    }

    public void invokePythonProcessForDartTask(Long raFileDetailsId) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getDartWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(), "--fileId",  "" + raFileDetailsId, "--rootPath",
                    appPropertiesConfig.getRootPath());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

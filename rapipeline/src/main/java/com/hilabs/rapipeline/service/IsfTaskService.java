package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RAFileDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.isfStatusCodes;

@Service
@Slf4j
public class IsfTaskService {
    private static Gson gson = new Gson();
    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> isfTaskRunningMap = new ConcurrentHashMap<>();

    public boolean shouldRun(Long raFileDetailsId) {
        if (isfTaskRunningMap.containsKey(raFileDetailsId)) {
            log.warn("IsfTask task in progress for raFileDetailsId {}", raFileDetailsId);
            return false;
        }
        return isFileIdEligibleForIsfTask(raFileDetailsId);
    }

    public List<RAFileDetails> getEligibleRAFileDetailsList(int count) {
        return raFileDetailsService.findFileDetailsByStatusCodes(isfStatusCodes, count, 0);
    }

    public boolean isFileIdEligibleForIsfTask(Long raFileDetailsId) {
        Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByRAFileDetailsId(raFileDetailsId);
        if (!optionalRAFileDetails.isPresent()) {
            return false;
        }
        RAFileDetails raFileDetails = optionalRAFileDetails.get();
        if (raFileDetails.getStatusCode() == null) {
            return false;
        }
        if (!isfStatusCodes.stream().anyMatch(p -> raFileDetails.getStatusCode().equals(p))) {
            return false;
        }
        return raFileDetails.getManualActionRequired() == null || raFileDetails.getManualActionRequired() == 0;
    }

    public void invokePythonProcessForIsfTask(Long raFileDetailsId) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getIsfWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(), "--fileId",  "" + raFileDetailsId, "--rootPath",
                    appPropertiesConfig.getRootPath());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

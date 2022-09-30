package com.rapipeline.preprocessing;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAFileXStatus;
import com.rapipeline.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class PreProcessingTask extends Task {
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;
    private FileSystemUtilService fileSystemUtilService;
    private RAFileDetailsService raFileDetailsService;
    private RAProviderService raProviderService;
    private RAFileXStatusService raFileXStatusService;

    private static final Gson gson = new Gson();

    public PreProcessingTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.fileSystemUtilService = (FileSystemUtilService) applicationContext.getBean("fileSystemUtilService");
        this.raFileMetaDataDetailsService = (RAFileMetaDataDetailsService) applicationContext.getBean("RAFileMetaDataDetailsService");
        this.raFileDetailsService = (RAFileDetailsService) applicationContext.getBean("RAFileDetailsService");
        this.raProviderService = (RAProviderService) applicationContext.getBean("RAProviderService");
        this.raFileXStatusService = (RAFileXStatusService) applicationContext.getBean("RAFileXStatusService");
    }

    @Override
    public void run() {
        log.info("PreProcessingTask stared for {}", gson.toJson(getTaskData()));
        Map<String, Object> taskData = getTaskData();
        if (!taskData.containsKey("data")) {
            log.warn("taskData doesn't have key data - taskData {}", gson.toJson(taskData));
            return;
        }
        if (!(taskData.get("data") instanceof RAFileXStatus)) {
            log.warn("data field in taskData doesn't have valid data - data {} taskData {}", gson.toJson(taskData.get("data")),
                    gson.toJson(taskData));
            return;
        }
        RAFileXStatus raFileXStatus = (RAFileXStatus) taskData.get("data");
        Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByRAFileDetailsId(raFileXStatus.getRaFileDetailsId());
        if (!optionalRAFileDetails.isPresent()) {
            log.warn("RAFileDetails not found for raFileXStatus {}", gson.toJson(raFileXStatus));
            return;
        }
        //TODO handle java errors
        RAFileDetails raFileDetails = optionalRAFileDetails.get();
        callProcessingApi(raFileDetails);
        log.debug("PreProcessingTask done for {}", gson.toJson(getTaskData()));
    }

    public boolean callProcessingApi(RAFileDetails raFileDetails) {
        //Yet to be implemented
        return true;
    }
}

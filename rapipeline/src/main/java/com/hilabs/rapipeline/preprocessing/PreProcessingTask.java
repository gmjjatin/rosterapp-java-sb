package com.hilabs.rapipeline.preprocessing;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.PythonInvocationService;
import com.hilabs.rapipeline.service.RAFileDetailsService;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.hilabs.rapipeline.preprocessing.PreprocessingUtils.preProcessingStatusCodes;
import static com.hilabs.roster.model.RosterSheetProcessStage.PRE_PROCESSING;
import static com.hilabs.roster.util.Constants.PRE_PROCESS_IN_PROGRESS;

@Slf4j
public class PreProcessingTask extends Task {
    private PythonInvocationService pythonInvocationService;

    private RAFileDetailsService raFileDetailsService;

    private DartRASystemErrorsService dartRASystemErrorsService;

    private static final Gson gson = new Gson();

    public static ConcurrentHashMap<Long, Boolean> runningMap = new ConcurrentHashMap<>();

    public PreProcessingTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.pythonInvocationService = (PythonInvocationService) applicationContext.getBean("pythonInvocationService");
        this.raFileDetailsService = (RAFileDetailsService) applicationContext.getBean("RAFileDetailsService");
    }

    public boolean shouldRun(Long raFileDetailsId) {
        if (runningMap.containsKey(raFileDetailsId)) {
            log.warn("PreProcessingTask task in progress for raFileDetailsId {}", raFileDetailsId);
            return false;
        }
        return isStillEligibleForRun(raFileDetailsId);
    }

    public boolean isStillEligibleForRun(Long raFileDetailsId) {
        Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findByRAFileDetailsId(raFileDetailsId);
        if (!optionalRAFileDetails.isPresent()) {
            return false;
        }
        RAFileDetails raFileDetails = optionalRAFileDetails.get();
        if (raFileDetails.getStatusCode() == null) {
            return false;
        }
        return preProcessingStatusCodes.stream().anyMatch(p -> raFileDetails.getStatusCode().equals(p));
    }

    @Override
    public void run() {
        log.info("PreProcessingTask stared for {}", gson.toJson(getTaskData()));
        Long raFileDetailsId = getRAFileDetailsIdFromTaskData();
        try {
            //TODO demo
//            if (!shouldRun(raFileDetailsId)) {
//                return;
//            }
            runningMap.put(raFileDetailsId, true);
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, PRE_PROCESS_IN_PROGRESS);
            //TODO change it
            pythonInvocationService.invokePythonProcessForPreProcessing(raFileDetailsId);
            log.debug("PreProcessingTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception | Error ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            dartRASystemErrorsService.saveDartRASystemErrors(raFileDetailsId, null,
                    PRE_PROCESSING.name(), null, "UNKNOWN", ex.getMessage(),
                    stacktrace, 1);
            log.error("Error in PreProcessingTask done for {} - message {} stacktrace {}", gson.toJson(getTaskData()),
                    ex.getMessage(), stacktrace);
        }
    }

    public Long getRAFileDetailsIdFromTaskData() {
        Map<String, Object> taskData = getTaskData();
        if (!taskData.containsKey("data")) {
            log.warn("taskData doesn't have key data - taskData {}", gson.toJson(taskData));
            return null;
        }
        if (!(taskData.get("data") instanceof Long)) {
            log.warn("data field in taskData doesn't have valid data - data {} taskData {}", gson.toJson(taskData.get("data")),
                    gson.toJson(taskData));
            return null;
        }
        return (Long) taskData.get("data");
    }
}

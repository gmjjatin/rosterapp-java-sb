package com.hilabs.rapipeline.preprocessing;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.PreProcessingTaskService;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.hilabs.rapipeline.service.PreProcessingTaskService.preProcessingRunningMap;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;

@Slf4j
public class PreProcessingTask extends Task {
    private PreProcessingTaskService preProcessingTaskService;

    private DartRASystemErrorsService dartRASystemErrorsService;

    private static final Gson gson = new Gson();

    public PreProcessingTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.preProcessingTaskService = (PreProcessingTaskService) applicationContext.getBean("preProcessingTaskService");
        this.dartRASystemErrorsService = (DartRASystemErrorsService) applicationContext.getBean("dartRASystemErrorsService");
    }

    @Override
    public void run() {
        log.info("PreProcessingTask started for {}", gson.toJson(getTaskData()));
        Long raFileDetailsId = getRAFileDetailsIdFromTaskData();
        try {
            //TODO demo
            if (!preProcessingTaskService.shouldRun(raFileDetailsId)) {
                return;
            }
            preProcessingRunningMap.put(raFileDetailsId, true);
            //TODO change it
            preProcessingTaskService.invokePythonProcessForPreProcessingTask(raFileDetailsId);
            log.info("PreProcessingTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception | Error ex) {
            log.error("Error in PreProcessingTask done for {} - message {} stacktrace {}", gson.toJson(getTaskData()),
                    ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 2000);
            dartRASystemErrorsService.saveDartRASystemErrors(raFileDetailsId, null,
                    "PRE PROCESSING", null, "UNKNOWN", ex.getMessage(),
                    stacktrace, 1);

        } finally {
            log.info("Finally in PreProcessing task for {}", gson.toJson(getTaskData()));
            preProcessingRunningMap.remove(raFileDetailsId);
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

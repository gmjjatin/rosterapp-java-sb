package com.hilabs.rapipeline.isf;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.IsfTaskService;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.hilabs.rapipeline.service.IsfTaskService.isfTaskRunningMap;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;

@Slf4j
public class IsfTask extends Task {
    private DartRASystemErrorsService dartRASystemErrorsService;
    private IsfTaskService isfTaskService;

    private static final Gson gson = new Gson();



    public IsfTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.dartRASystemErrorsService = (DartRASystemErrorsService) applicationContext.getBean("dartRASystemErrorsService");
        this.isfTaskService = (IsfTaskService) applicationContext.getBean("ISFTaskService");
    }

    @Override
    public void run() {
        log.info("IsfTask stared for {}", gson.toJson(getTaskData()));
        Long raFileDetailsId = getRAFileDetailsIdFromTaskData();
        try {
            if (!isfTaskService.shouldRun(raFileDetailsId)) {
                return;
            }
            isfTaskRunningMap.put(raFileDetailsId, true);
            //TODO change it
            isfTaskService.invokePythonProcessForIsfTask(raFileDetailsId);
            log.debug("IsfTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception | Error ex) {
            String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 2000);
            dartRASystemErrorsService.saveDartRASystemErrors(raFileDetailsId, null,
                    "ISF", null, "UNKNOWN", ex.getMessage(),
                    stacktrace, 1);
            log.error("Error in IsfTask done for {} - message {} stacktrace {}", gson.toJson(getTaskData()),
                    ex.getMessage(), stacktrace);
        } finally {
            isfTaskRunningMap.remove(raFileDetailsId);
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

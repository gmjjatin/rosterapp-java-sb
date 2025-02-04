package com.hilabs.rapipeline.dart;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.DartTaskService;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.hilabs.rapipeline.service.DartTaskService.dartTaskRunningMap;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;

@Slf4j
public class DartTask extends Task {
    private DartRASystemErrorsService dartRASystemErrorsService;
    private DartTaskService dartTaskService;

    private static final Gson gson = new Gson();

    public DartTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.dartRASystemErrorsService = (DartRASystemErrorsService) applicationContext.getBean("dartRASystemErrorsService");
        this.dartTaskService = (DartTaskService) applicationContext.getBean("dartTaskService");
    }

    @Override
    public void run() {
        log.info("DartTask started for {}", gson.toJson(getTaskData()));
        RASheetDetails raSheetDetails = getRASheetDetailsFromTaskData();
        if (raSheetDetails == null) {
            return;
        }
        try {
            if (!dartTaskService.shouldRun(raSheetDetails, false)) {
                return;
            }
            dartTaskRunningMap.put(raSheetDetails.getId(), true);
            //TODO change it
            dartTaskService.invokePythonProcessForDartTask(raSheetDetails);
            log.debug("DartTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception | Error ex) {
            String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 2000);
            dartRASystemErrorsService.saveDartRASystemErrors(raSheetDetails.getRaFileDetailsId(), raSheetDetails.getId(),
                    "DART", null, "UNKNOWN", ex.getMessage(),
                    stacktrace, 1);
            log.error("Error in DartTask done for {} - message {} stacktrace {}", gson.toJson(getTaskData()),
                    ex.getMessage(), stacktrace);
        } finally {
            dartTaskRunningMap.remove(raSheetDetails.getId());
        }
    }

    public RASheetDetails getRASheetDetailsFromTaskData() {
        Map<String, Object> taskData = getTaskData();
        //Core framework level errors should change anything related to file
        if (!taskData.containsKey("data")) {
            log.warn("taskData doesn't have key data - taskData {}", gson.toJson(taskData));
            return null;
        }
        if (!(taskData.get("data") instanceof RASheetDetails)) {
            log.warn("data field in taskData doesn't have valid data - data {} taskData {}", gson.toJson(taskData.get("data")),
                    gson.toJson(taskData));
            return null;
        }
        return (RASheetDetails) taskData.get("data");
    }
}

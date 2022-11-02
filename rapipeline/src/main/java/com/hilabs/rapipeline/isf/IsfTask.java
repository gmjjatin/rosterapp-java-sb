package com.hilabs.rapipeline.isf;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.IsfTaskService;
import com.hilabs.roster.entity.RASheetDetails;
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
        this.isfTaskService = (IsfTaskService) applicationContext.getBean("isfTaskService");
    }

    @Override
    public void run() {
        log.info("IsfTask started for {}", gson.toJson(getTaskData()));
        RASheetDetails raSheetDetails = getRASheetDetailsFromTaskData();
        if (raSheetDetails == null) {
            return;
        }
        try {
            isfTaskRunningMap.put(raSheetDetails.getId(), true);
            //TODO change it
            isfTaskService.invokePythonProcessForIsfTask(raSheetDetails);
            isfTaskService.consolidateISF(raSheetDetails.getRaFileDetailsId());
            log.debug("IsfTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception | Error ex) {
            try {
                log.error("Error in IsfTask done for {} - message {} stacktrace {}", gson.toJson(getTaskData()),
                        ex.getMessage(), ExceptionUtils.getStackTrace(ex));
                String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 2000);
                dartRASystemErrorsService.saveDartRASystemErrors(raSheetDetails.getRaFileDetailsId(), raSheetDetails.getId(),
                        "ISF", null, "UNKNOWN", ex.getMessage(),
                        stacktrace, 1);
            } catch (Exception ignore) {
                log.error("Error in IsfTask catch - message {} stacktrace {}", ignore.getMessage(),
                        ExceptionUtils.getStackTrace(ignore));
            }
        } finally {
            log.info("Finally in Isf task for {}", gson.toJson(getTaskData()));
            isfTaskRunningMap.remove(raSheetDetails.getId());
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

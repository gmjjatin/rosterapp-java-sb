package com.hilabs.rapipeline.sps;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.SpsTaskService;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.hilabs.rapipeline.service.FileSystemUtilService.getListOfFilesInFolder;
import static com.hilabs.rapipeline.service.SpsTaskService.spsTaskRunningMap;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.dartUIValidationInProgressSheetStatusCode;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.readyForSpsSheetStatusCode;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;

@Slf4j
public class SpsTask extends Task {
    private DartRASystemErrorsService dartRASystemErrorsService;
    private SpsTaskService spsTaskService;
    private RASheetDetailsRepository raSheetDetailsRepository;

    private static final Gson gson = new Gson();

    public SpsTask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.dartRASystemErrorsService = (DartRASystemErrorsService) applicationContext.getBean("dartRASystemErrorsService");
        this.spsTaskService = (SpsTaskService) applicationContext.getBean("spsTaskService");
        this.raSheetDetailsRepository = (RASheetDetailsRepository) applicationContext.getBean("RASheetDetailsRepository");
    }

    @Override
    public void run() {
        log.info("SpsTask started for {}", gson.toJson(getTaskData()));
        RASheetDetails raSheetDetails = getRASheetDetailsFromTaskData();
        if (raSheetDetails == null) {
            return;
        }
        try {
            spsTaskRunningMap.put(raSheetDetails.getId(), true);
            Optional<String> filePathOptional = spsTaskService.checkAndGetSpsResponseFilePathIfExists(raSheetDetails);
            if (!filePathOptional.isPresent()) {
                log.info("Response file doesn't exist for raSheetDetails {} - so skipping sps task", gson.toJson(raSheetDetails));
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        readyForSpsSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            log.info("Response file exist for raSheetDetails {} filePath {}", gson.toJson(raSheetDetails), filePathOptional.get());
            //TODO change it
            spsTaskService.copySpsResponseFileToDestination(filePathOptional.get());
            log.info("copySpsResponseFileToDestination done for raSheetDetails {} filePath {}", gson.toJson(raSheetDetails), filePathOptional.get());
            spsTaskService.invokePythonProcessForSpsTask(raSheetDetails);
            log.debug("SpsTask done for {}", gson.toJson(getTaskData()));
        } catch (Exception | Error ex) {
            try {
                log.error("Error in SpsTask done for {} - message {} stacktrace {}", gson.toJson(getTaskData()),
                        ex.getMessage(), ExceptionUtils.getStackTrace(ex));
                String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 2000);
                dartRASystemErrorsService.saveDartRASystemErrors(raSheetDetails.getRaFileDetailsId(), raSheetDetails.getId(),
                        "SPS", null, "UNKNOWN", ex.getMessage(),
                        stacktrace, 1);
            } catch (Exception ignore) {
                log.error("Error in SpsTask catch - message {} stacktrace {}", ignore.getMessage(),
                        ExceptionUtils.getStackTrace(ignore));
            }
        } finally {
            log.info("Finally in SpsTask task for {}", gson.toJson(getTaskData()));
            spsTaskRunningMap.remove(raSheetDetails.getId());
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

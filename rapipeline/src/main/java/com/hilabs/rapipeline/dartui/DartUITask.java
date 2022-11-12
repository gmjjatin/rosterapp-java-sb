package com.hilabs.rapipeline.dartui;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.model.DartStatusCheckResponse;
import com.hilabs.rapipeline.service.DartUITaskService;
import com.hilabs.rapipeline.service.FileSystemUtilService;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static com.hilabs.rapipeline.service.DartUITaskService.dartUITaskRunningMap;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.dartUIFeedbackReceived;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.dartUIValidationInProgressSheetStatusCode;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;

@Slf4j
public class DartUITask extends Task {
    private DartRASystemErrorsService dartRASystemErrorsService;
    private DartUITaskService dartUITaskService;
    private FileSystemUtilService fileSystemUtilService;

    private RestTemplate restTemplate;

    private RASheetDetailsRepository raSheetDetailsRepository;

    private static final Gson gson = new Gson();

    public DartUITask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.fileSystemUtilService = (FileSystemUtilService) applicationContext.getBean("fileSystemUtilService");
        this.dartRASystemErrorsService = (DartRASystemErrorsService) applicationContext.getBean("dartRASystemErrorsService");
        this.dartUITaskService = (DartUITaskService) applicationContext.getBean("dartUITaskService");
        this.raSheetDetailsRepository = (RASheetDetailsRepository) applicationContext.getBean("RASheetDetailsRepository");
    }

    @Override
    public void run() {
        log.info("DartUITask started for {}", gson.toJson(getTaskData()));
        RASheetDetails raSheetDetails = getRASheetDetailsFromTaskData();
        if (raSheetDetails == null) {
            return;
        }
        try {
            dartUITaskRunningMap.put(raSheetDetails.getId(), true);
            String validationFileId = raSheetDetails.getValidationFileId();
            //TODO confirm
            if (validationFileId == null) {
                log.error("validationFileId is null for raSheetDetails {} so skipping dart ui task", gson.toJson(raSheetDetails));
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            //TODO handle with and without bad file
            DartStatusCheckResponse dartStatusCheckResponse = dartUITaskService.checkDartUIStatusOfSheet(validationFileId);
            String status = dartStatusCheckResponse.getStatus();
            boolean isValidationCompleted = (status != null) && (status.equalsIgnoreCase("Ready to Submit")
                    || status.equalsIgnoreCase("Ready for Review"));

            if (!isValidationCompleted) {
                log.info("Status for raSheetDetails {} is {} - so skipping dart ui task", gson.toJson(raSheetDetails), status);
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            //TODO
            if (status.equalsIgnoreCase("Ready for Review")) {
                String fileType = "Reviewed";
                String dartUIFileName = dartUITaskService.downloadDartUIResponseFile(validationFileId,
                        fileSystemUtilService.getDartUIResponseFolderPath(), fileType);
                if (dartUIFileName == null) {
                    log.error("downloadDartUIResponseFile is not successful for raSheetDetails {} so skipping dart ui task", gson.toJson(raSheetDetails));
                    raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                            dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                    return;
                }
                raSheetDetails.setValidationFileName(dartUIFileName);
            }
            raSheetDetails.setStatusCode(dartUIFeedbackReceived);
            raSheetDetailsRepository.save(raSheetDetails);
            dartUITaskService.invokePythonProcessForDartUITask(raSheetDetails);
            dartUITaskService.consolidateDartUIValidation(raSheetDetails.getRaFileDetailsId());
            log.debug("DartUITask done for {}", gson.toJson(getTaskData()));
        } catch (Exception | Error ex) {
            try {
                log.error("Error in DartUITask done for {} - message {} stacktrace {}", gson.toJson(getTaskData()),
                        ex.getMessage(), ExceptionUtils.getStackTrace(ex));
                String stacktrace = trimToNChars(ExceptionUtils.getStackTrace(ex), 2000);
                dartRASystemErrorsService.saveDartRASystemErrors(raSheetDetails.getRaFileDetailsId(), raSheetDetails.getId(),
                        "DART_UI", null, "UNKNOWN", ex.getMessage(),
                        stacktrace, 1);
            } catch (Exception ignore) {
                log.error("Error in DartUITask catch - message {} stacktrace {}", ignore.getMessage(),
                        ExceptionUtils.getStackTrace(ignore));
            }
        } finally {
            log.info("Finally in DartUITask task for {}", gson.toJson(getTaskData()));
            dartUITaskRunningMap.remove(raSheetDetails.getId());
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

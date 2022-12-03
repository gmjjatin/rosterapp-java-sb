package com.hilabs.rapipeline.dartui;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.model.DartStatusCheckResponse;
import com.hilabs.rapipeline.model.DartUIAuthResponse;
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
import java.util.Optional;

import static com.hilabs.rapipeline.service.DartUITaskService.dartUITaskRunningMap;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.*;
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
                log.error("Skipping dart ui task - validationFileId is null for raSheetDetails {}", gson.toJson(raSheetDetails));
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            Optional<DartUIAuthResponse> optionalDartUIAuthResponse = dartUITaskService.getDartUIJwtToken();
            if (!optionalDartUIAuthResponse.isPresent() || optionalDartUIAuthResponse.get().getToken() == null) {
                log.error("Skipping dart ui task - received non 200 status from auth api -  for raSheetDetails {}", gson.toJson(raSheetDetails));
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            DartUIAuthResponse dartUIAuthResponse = optionalDartUIAuthResponse.get();
            String dartUIToken = dartUIAuthResponse.getToken();
            //TODO handle with and without bad file
            Optional<DartStatusCheckResponse> optionalDartStatusCheckResponse = dartUITaskService.checkDartUIStatusOfSheet(validationFileId, dartUIToken);
            if (!optionalDartStatusCheckResponse.isPresent()) {
                log.error("Skipping dart ui task - received non 200 status -  for raSheetDetails {}", gson.toJson(raSheetDetails));
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            DartStatusCheckResponse dartStatusCheckResponse = optionalDartStatusCheckResponse.get();
            String status = dartStatusCheckResponse.getReviewStatus();
            boolean isValidationCompleted = (status != null) && (status.equalsIgnoreCase("Ready to Submit")
                    || status.equalsIgnoreCase("Ready for Review")
                    || status.equalsIgnoreCase("Submitted"));

            if (!isValidationCompleted) {
                log.info("Skipping dart ui task - isValidationCompleted false - Status for raSheetDetails {} is {}", gson.toJson(raSheetDetails), status);
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            String fileType = "Reviewed";
            String dartUIFileName = dartUITaskService.downloadDartUIResponseFile(validationFileId,
                    fileSystemUtilService.getDartUIResponseFolderPath(), fileType, dartUIToken);
            if (dartUIFileName == null) {
                log.info("Skipping dart ui task - downloadDartUIResponseFile is not successful - raSheetDetails {}", gson.toJson(raSheetDetails));
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Collections.singletonList(raSheetDetails.getId()),
                        dartUIValidationInProgressSheetStatusCode, "SYSTEM", new Date());
                return;
            }
            raSheetDetails.setValidationFileName(dartUIFileName);
            raSheetDetails.setStatusCode(dartUIFeedbackReceived);
            log.info("Saving raSheetDetails {}", gson.toJson(raSheetDetails));
            raSheetDetails = raSheetDetailsRepository.saveAndFlush(raSheetDetails);
            log.info("raSheetDetails saved {}", gson.toJson(raSheetDetails));
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

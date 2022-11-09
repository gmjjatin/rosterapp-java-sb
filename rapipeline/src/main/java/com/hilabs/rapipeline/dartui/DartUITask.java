package com.hilabs.rapipeline.dartui;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.model.DartStatusCheckResponse;
import com.hilabs.rapipeline.service.DartUITaskService;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static com.hilabs.rapipeline.service.DartUITaskService.dartUITaskRunningMap;
import static com.hilabs.rapipeline.service.FileSystemUtilService.downloadUsingNIO;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.dartUISheetStatusCode;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.dartUISheetTaskStatusCode;
import static com.hilabs.rapipeline.util.Utils.trimToNChars;

@Slf4j
public class DartUITask extends Task {
    private DartRASystemErrorsService dartRASystemErrorsService;
    private DartUITaskService dartUITaskService;

    private RestTemplate restTemplate;

    private RASheetDetailsRepository raSheetDetailsRepository;

    private static final Gson gson = new Gson();

    public DartUITask(Map<String, Object> taskData) {
        super(taskData);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
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
            raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Arrays.asList(raSheetDetails.getId()), dartUISheetTaskStatusCode,
                    "SYSTEM", new Date());

            DartStatusCheckResponse dartStatusCheckResponse = dartUITaskService.checkDartUIStatusOfSheet(raSheetDetails);
            boolean isDone = dartStatusCheckResponse.getStatus() != null && dartStatusCheckResponse.getStatus().equals("completed");
            if (!isDone) {
                raSheetDetailsRepository.updateRASheetDetailsStatusByIds(Arrays.asList(raSheetDetails.getId()), dartUISheetStatusCode,
                        "SYSTEM", new Date());
                return;
            }
            //TODO demo
            raSheetDetails.setDartUIFileName("dart_file_name");
            raSheetDetailsRepository.save(raSheetDetails);
            dartUITaskService.downloadDartUIResponseFile(raSheetDetails);
            //TODO change it
            dartUITaskService.invokePythonProcessForDartUITask(raSheetDetails);
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

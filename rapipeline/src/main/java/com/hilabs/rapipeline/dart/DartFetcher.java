package com.hilabs.rapipeline.dart;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.*;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DartFetcher implements JobRetriever {
    private static final Gson gson = new Gson();

    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private RAFileStatusUpdatingService raFileStatusUpdatingService;

    @Autowired
    private RASheetDetailsService raSheetDetailsService;

    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private DartTaskService dartTaskService;

    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;
    @Autowired
    private ApplicationContext applicationContext;

    //    27, 31
    @Override
    public List<Task> refillQueue(Integer tasks) {
        log.info("DartFetcher started - tasks {}", tasks);
        try {
            List<RAFileDetails> raFileDetailsList = dartTaskService.getEligibleRAFileDetailsList(Math.max(tasks * 2, 50));
            log.info("DartFetcher - raFileDetailsList size {}", raFileDetailsList.size());
            List<Task> executors = new ArrayList<>();
            int count = 0;
            List<Long> inCompatibleFileIdList = new ArrayList<>();
            List<Long> pickedFileIdList = new ArrayList<>();
            List<Long> newlyAddedSheetIdList = new ArrayList<>();
            for (RAFileDetails raFileDetails : raFileDetailsList) {
                Long raFileDetailsId = raFileDetails.getId();
                List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
                boolean isCompatible = raFileStatusUpdatingService.checkCompatibleOrNotAndUpdateFileStatusForDart(raFileDetailsId, raSheetDetailsList);
                if (!isCompatible) {
//                    log.info("raFileDetails {}  is not eligible for Dart", raFileDetails);
                    inCompatibleFileIdList.add(raFileDetails.getId());
                    continue;
                }
                pickedFileIdList.add(raFileDetails.getId());
//                log.info("Picked raFileDetails {} for DART", raFileDetails);
                if (raFileDetails.getStatusCode() == 35) {
                    raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 41);
                }
                for (RASheetDetails raSheetDetails : raSheetDetailsList) {
                    Long raSheetDetailsId = raSheetDetails.getId();
                    if (!dartTaskService.shouldRun(raSheetDetails, true)) {
                        continue;
                    }
                    count++;
                    Map<String, Object> taskData = new HashMap<>();
                    taskData.put("data", raSheetDetails);
                    DartTask dartTask = new DartTask(taskData);
                    dartTask.setApplicationContext(applicationContext);
                    executors.add(dartTask);
                    raSheetDetailsService.updateRASheetDetailsStatus(raSheetDetailsId, 160);
                    newlyAddedSheetIdList.add(raSheetDetails.getId());
                    if (count >= tasks) {
                        break;
                    }
                }
            }
            log.info("Dart inCompatibleFileIdList {} pickedFileIdList {} newlyAddedSheetIdList {}",
                    gson.toJson(inCompatibleFileIdList), gson.toJson(pickedFileIdList), gson.toJson(newlyAddedSheetIdList));
            log.info("DartFetcher ended - tasks {} executors {}", tasks, executors.size());
            return executors;
        } catch (Exception ex) {
            log.error("Error DartFetcher {} stackTrace {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }
}
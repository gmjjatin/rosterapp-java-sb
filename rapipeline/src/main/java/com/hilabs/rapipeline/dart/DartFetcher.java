package com.hilabs.rapipeline.dart;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.*;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
            List<RASheetDetails> raSheetDetailsList = dartTaskService.getEligibleRAFileDetailsList(tasks);
            log.info("DartFetcher - raSheetDetailsList size {}", raSheetDetailsList.size());
            List<Task> executors = new ArrayList<>();
            List<Long> inCompatibleFileIdList = new ArrayList<>();
            List<Long> pickedFileIdList = new ArrayList<>();
            List<Long> newlyAddedSheetIdList = new ArrayList<>();
            for (RASheetDetails raSheetDetails : raSheetDetailsList) {
                Long raFileDetailsId = raSheetDetails.getRaFileDetailsId();
                raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 41);
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("data", raSheetDetails);
                DartTask dartTask = new DartTask(taskData);
                dartTask.setApplicationContext(applicationContext);
                executors.add(dartTask);
                newlyAddedSheetIdList.add(raSheetDetails.getId());
            }
            log.info("Dart inCompatibleFileIdList {} pickedFileIdList {} newlyAddedSheetIdList {}",
                    gson.toJson(inCompatibleFileIdList), gson.toJson(pickedFileIdList), gson.toJson(newlyAddedSheetIdList));
            log.info("DartFetcher ended - tasks {} executors {}", tasks, executors.size());
            return executors;
        } catch (Exception ex) {
            log.error("Error DartFetcher {} stackTrace {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            return new ArrayList<>();
        }
    }
}
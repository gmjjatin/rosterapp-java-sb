package com.hilabs.rapipeline.dartui;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.*;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hilabs.rapipeline.service.FileSystemUtilService.downloadUsingNIO;

@Component
@Slf4j
public class DartUIFetcher implements JobRetriever {
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
    private DartUITaskService dartUITaskService;

    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;
    @Autowired
    private ApplicationContext applicationContext;

//    27, 31
    @Override
    public List<Task> refillQueue(Integer tasks) {
        log.info("DartUIFetcher started - tasks {}", tasks);
        try {
            List<RASheetDetails> raSheetDetailsList = dartUITaskService.getEligibleRASheetDetailsListAndUpdate(tasks);
            log.info("raSheetDetailsList size {}", raSheetDetailsList.size());
            List<Task> executors = new ArrayList<>();
            List<Long> inCompatibleFileIdList = new ArrayList<>();
            List<Long> pickedFileIdList = new ArrayList<>();
            List<Long> newlyAddedSheetIdList = new ArrayList<>();
            for (RASheetDetails raSheetDetails : raSheetDetailsList) {
                //TODO demo
//                raFileDetailsService.updateRAFileDetailsStatus(raSheetDetails.getRaFileDetailsId(), 31);
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("data", raSheetDetails);
                DartUITask dartUITask = new DartUITask(taskData);
                dartUITask.setApplicationContext(applicationContext);
                executors.add(dartUITask);
                newlyAddedSheetIdList.add(raSheetDetails.getId());
            }
            log.info("Dart UI inCompatibleFileIdList {} pickedFileIdList {} newlyAddedSheetIdList {}",
                    gson.toJson(inCompatibleFileIdList), gson.toJson(pickedFileIdList), gson.toJson(newlyAddedSheetIdList));
            log.info("DartUIFetcher ended - tasks {} executors size {}", tasks, executors.size());
            return executors;
        } catch (Exception ex) {
            log.error("Error DartUIFetcher {}", ex.getMessage());
            return new ArrayList<>();
        }
    }
}

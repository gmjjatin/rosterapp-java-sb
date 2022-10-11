package com.hilabs.rapipeline.preprocessing;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.RAFileDetailsService;
import com.hilabs.rapipeline.service.RAFileMetaDataDetailsService;
import com.hilabs.roster.entity.RAFileDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.hilabs.rapipeline.preprocessing.PreProcessingFirstJobTask.preProcessingFirstJobTaskRunningMap;
import static com.hilabs.rapipeline.preprocessing.PreprocessingUtils.preProcessingStatusCodes;

@Component
@Slf4j
public class PreProcessingFirstJobFetcher implements JobRetriever {
    private static final Gson gson = new Gson();

    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private RAFileDetailsService raFileDetailsService;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        try {
            List<RAFileDetails> raFileDetailsList = raFileDetailsService.
                    findFileDetailsByStatusCodes(preProcessingStatusCodes, tasks, 0);
            log.info("raFileDetailsList size {}", raFileDetailsList.size());
            List<Task> executors = new ArrayList<>();
            for (RAFileDetails raFileDetails : raFileDetailsList) {
                if (preProcessingFirstJobTaskRunningMap.containsKey(raFileDetails.getId())) {
                    continue;
                }
                Map<String, Object> taskData = new HashMap<>();
//                taskData.put("id", "" + raFileDetails.getId());
                taskData.put("data", raFileDetails.getId());
                PreProcessingFirstJobTask preProcessingFirstJobTask = new PreProcessingFirstJobTask(taskData);
                preProcessingFirstJobTask.setApplicationContext(applicationContext);
                executors.add(preProcessingFirstJobTask);
            }
            return executors;
        } catch (Exception ex) {
            log.error("Error PreProcessingFirstJobFetcher {}", ex.getMessage());
            throw ex;
        }
    }
}

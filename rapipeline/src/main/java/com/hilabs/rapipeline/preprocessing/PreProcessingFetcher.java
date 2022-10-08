package com.hilabs.rapipeline.preprocessing;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.RAFileDetailsService;
import com.hilabs.rapipeline.service.RAFileMetaDataDetailsService;
import com.hilabs.roster.entity.RAFileDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.hilabs.rapipeline.preprocessing.PreprocessingUtils.preProcessingStatusCodes;
import static com.hilabs.roster.util.Constants.ROSTER_INGESTION_COMPLETED;

@Component
public class PreProcessingFetcher implements JobRetriever {
    private static final Gson gson = new Gson();

    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private RAFileDetailsService raFileDetailsService;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        List<RAFileDetails> raFileDetailsList = raFileDetailsService.
                findFileDetailsByStatusCodes(preProcessingStatusCodes, tasks, 0);
        List<Task> executors = new ArrayList<>();
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("id", "1");
        taskData.put("data", 1L);
        PreProcessingTask preProcessingTask = new PreProcessingTask(taskData);
        preProcessingTask.setApplicationContext(applicationContext);
        executors.add(preProcessingTask);
//        for (RAFileDetails raFileDetails : raFileDetailsList) {
//            Map<String, Object> taskData = new HashMap<>();
////            taskData.put("id", raFileMetaData.getFileName());
//            taskData.put("data", raFileDetails.getId());
//            PreProcessingTask preProcessingTask = new PreProcessingTask(taskData);
//            preProcessingTask.setApplicationContext(applicationContext);
//            executors.add(preProcessingTask);
//        }
        return executors;
    }
}

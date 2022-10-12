package com.hilabs.rapipeline.ingestion;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.dto.RAFileMetaData;
import com.hilabs.rapipeline.service.IngestionTaskService;
import com.hilabs.rapipeline.service.RAFileDetailsService;
import com.hilabs.rapipeline.service.RAFileMetaDataDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IngestionFetcher implements JobRetriever {
    private static final Gson gson = new Gson();
    private static final int MAX_RETRY_NO = 1;
    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private IngestionTaskService ingestionTaskService;


    @Autowired
    private ApplicationContext applicationContext;

    //TODO validate metadata
    @Override
    public List<Task> refillQueue(Integer tasks) {
        List<Task> executors = new ArrayList<>();
        List<RAFileMetaData> raFileMetaDataList = raFileMetaDataDetailsService
                .getNewAndReProcessFileMetaDataDetails(2 * tasks);
        int count = 0;
        for (RAFileMetaData raFileMetaData : raFileMetaDataList) {
            if (!ingestionTaskService.shouldRun(raFileMetaData)) {
                continue;
            }
            count++;
            Map<String, Object> taskData = new HashMap<>();
//            taskData.put("id", raFileMetaData.getFileName());
            taskData.put("data", raFileMetaData);
            IngestionTask ingestionTask = new IngestionTask(taskData);
            ingestionTask.setApplicationContext(applicationContext);
            executors.add(ingestionTask);
            if (count >= tasks) {
                break;
            }
        }
        return executors;
    }
}


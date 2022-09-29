package com.rapipeline.ingestion;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.rapipeline.dto.RAFileMetaData;
import com.rapipeline.repository.RAProvDetailsRepository;
import com.rapipeline.service.RAFileDetailsService;
import com.rapipeline.service.RAFileMetaDataDetailsService;
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
    private RAProvDetailsRepository raProvDetailsRepository;


    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        List<Task> executors = new ArrayList<>();
        List<RAFileMetaData> raFileMetaDataList = raFileMetaDataDetailsService
                .getUnIngestedRAFileMetaDataDetails();
        for (RAFileMetaData raFileMetaData : raFileMetaDataList) {
            Map<String, Object> taskData = new HashMap<>();
//            taskData.put("id", String.valueOf(raFileMetaDataDetails.getId()));
            taskData.put("data", raFileMetaData);
            IngestionTask ingestionTask = new IngestionTask(taskData);
            ingestionTask.setApplicationContext(applicationContext);
            executors.add(ingestionTask);
        }
        return executors;
    }
}

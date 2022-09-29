package com.rapipeline.preprocessing;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.rapipeline.dto.RAFileMetaData;
import com.rapipeline.entity.RAFileXStatus;
import com.rapipeline.repository.RAProvDetailsRepository;
import com.rapipeline.service.RAFileDetailsService;
import com.rapipeline.service.RAFileMetaDataDetailsService;
import com.rapipeline.service.RAFileXStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rapipeline.util.PipelineStatusCodeUtil.INGESTED_STATUS_CODE;

@Component
public class PreProcessingFetcher implements JobRetriever {
    private static final Gson gson = new Gson();
    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private RAFileXStatusService raFileXStatusService;

    @Autowired
    private RAProvDetailsRepository raProvDetailsRepository;


    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        List<Task> executors = new ArrayList<>();
        List<RAFileXStatus> raFileXStatusList = raFileXStatusService.findRAFileXStatusWithCode(INGESTED_STATUS_CODE);
        for (RAFileXStatus raFileXStatus : raFileXStatusList) {
            Map<String, Object> taskData = new HashMap<>();
//            taskData.put("id", String.valueOf(raFileMetaDataDetails.getId()));
            taskData.put("data", raFileXStatus);
            PreProcessingTask preProcessingTask = new PreProcessingTask(taskData);
            preProcessingTask.setApplicationContext(applicationContext);
            executors.add(preProcessingTask);
        }
        return executors;
    }
}

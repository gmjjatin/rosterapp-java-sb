package com.hilabs.rapipeline.ingestion;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.IngestionTaskService;
import com.hilabs.rapipeline.service.RAFileDetailsService;
import com.hilabs.rapipeline.service.RAFileMetaDataDetailsService;
import com.hilabs.roster.dto.RAFileMetaData;
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
        try {
            log.info("IngestionFetcher started - tasks {}", tasks);
            List<Task> executors = new ArrayList<>();
            List<RAFileMetaData> raFileMetaDataList = raFileMetaDataDetailsService.getNewFileMetaDataDetailsAndUpdateToInQueue(tasks);
                for (RAFileMetaData raFileMetaData : raFileMetaDataList) {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("data", raFileMetaData);
                IngestionTask ingestionTask = new IngestionTask(taskData);
                ingestionTask.setApplicationContext(applicationContext);
                executors.add(ingestionTask);
            }
            log.info("IngestionFetcher ended - tasks {} executors size {}", tasks, executors.size());
            return executors;
        } catch (Exception ex) {
            log.error("Error IngestionFetcher {} stackTrace {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            return new ArrayList<>();
        }
    }
}


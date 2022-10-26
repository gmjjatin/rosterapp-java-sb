package com.hilabs.rapipeline.preprocessing;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.PreProcessingTaskService;
import com.hilabs.roster.entity.RAFileDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class TestFetcher implements JobRetriever {
    private static final Gson gson = new Gson();
    @Autowired
    private PreProcessingTaskService preProcessingTaskService;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        log.info("TestFetcher started tasks {}", tasks);
        try {
            List<Task> executors = new ArrayList<>();
            for (int i = 0; i < tasks; i++) {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("date", new Date());
                executors.add(new TestTask(taskData));
            }
            return executors;
        } catch (Exception ex) {
            log.error("Error PreProcessingFetcher {}", ex.getMessage());
            throw ex;
        }
    }
}

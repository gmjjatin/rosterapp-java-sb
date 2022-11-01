package com.hilabs.rapipeline.preprocessing;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.PreProcessingTaskService;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hilabs.rapipeline.service.PreProcessingTaskService.preProcessingRunningMap;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.preProcessingInQueueStatus;

@Component
@Slf4j
public class PreProcessingFetcher implements JobRetriever {
    private static final Gson gson = new Gson();
    @Autowired
    private PreProcessingTaskService preProcessingTaskService;

    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        log.info("PreProcessingFetcher started - tasks {}", tasks);
        try {
            List<RAFileDetails> raFileDetailsList = preProcessingTaskService.getEligibleRAFileDetailsListAndUpdate(tasks);
            log.info("raFileDetailsList size {} preProcessingRunningMap {} tasks {}", raFileDetailsList.size(),
                    gson.toJson(preProcessingRunningMap), tasks);
            List<Task> executors = new ArrayList<>();
            for (RAFileDetails raFileDetails : raFileDetailsList) {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("data", raFileDetails.getId());
                PreProcessingTask preProcessingTask = new PreProcessingTask(taskData);
                preProcessingTask.setApplicationContext(applicationContext);
                executors.add(preProcessingTask);
            }
            log.info("PreProcessingFetcher ended - tasks {} executors size {}", tasks, executors.size());
            return executors;
        } catch (Exception ex) {
            log.error("Error PreProcessingFetcher {}", ex.getMessage());
            return new ArrayList<>();
        }
    }
}

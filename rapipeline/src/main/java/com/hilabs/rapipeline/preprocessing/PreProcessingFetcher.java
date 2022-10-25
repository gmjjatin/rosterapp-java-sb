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
        try {
            List<RAFileDetails> raFileDetailsList = preProcessingTaskService.getEligibleRAFileDetailsList(tasks);
            log.info("raFileDetailsList size {} preProcessingRunningMap {} tasks {}", raFileDetailsList.size(), gson.toJson(preProcessingRunningMap),
                    tasks);
            List<Task> executors = new ArrayList<>();
            int count = 0;
            for (RAFileDetails raFileDetails : raFileDetailsList) {
//                if (!preProcessingTaskService.shouldRun(raFileDetails.getId())) {
//                    continue;
//                }
                count++;
                Map<String, Object> taskData = new HashMap<>();
//                taskData.put("id", "" + raFileDetails.getId());
                taskData.put("data", raFileDetails.getId());
                raFileDetailsRepository.updateRAFileDetailsStatus(raFileDetails.getId(), preProcessingInQueueStatus);
                PreProcessingTask preProcessingTask = new PreProcessingTask(taskData);
                preProcessingTask.setApplicationContext(applicationContext);
                executors.add(preProcessingTask);
                if (count >= tasks) {
                    break;
                }
            }
            return executors;
        } catch (Exception ex) {
            log.error("Error PreProcessingFetcher {}", ex.getMessage());
            throw ex;
        }
    }
}

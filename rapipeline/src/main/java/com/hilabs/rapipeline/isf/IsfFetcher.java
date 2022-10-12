package com.hilabs.rapipeline.isf;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.IsfTaskService;
import com.hilabs.rapipeline.service.RAFileMetaDataDetailsService;
import com.hilabs.roster.entity.RAFileDetails;
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
public class IsfFetcher implements JobRetriever {
    private static final Gson gson = new Gson();

    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private IsfTaskService isfTaskService;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        try {
            List<RAFileDetails> raFileDetailsList = isfTaskService.getEligibleRAFileDetailsList(tasks * 2);
            log.info("raFileDetailsList size {}", raFileDetailsList.size());
            List<Task> executors = new ArrayList<>();
            int count = 0;
            for (RAFileDetails raFileDetails : raFileDetailsList) {
                if (!isfTaskService.shouldRun(raFileDetails.getId())) {
                    continue;
                }
                count++;
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("data", raFileDetails.getId());
                IsfTask isfTask = new IsfTask(taskData);
                isfTask.setApplicationContext(applicationContext);
                executors.add(isfTask);
                if (count >= tasks) {
                    break;
                }
            }
            return executors;
        } catch (Exception ex) {
            log.error("Error IsfFetcher {}", ex.getMessage());
            throw ex;
        }
    }
}

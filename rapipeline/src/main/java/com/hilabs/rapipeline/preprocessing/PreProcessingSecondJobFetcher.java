package com.hilabs.rapipeline.preprocessing;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.PreprocessingService;
import com.hilabs.rapipeline.service.RAFileDetailsService;
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

import static com.hilabs.rapipeline.preprocessing.PreprocessingUtils.preProcessingJob2StatusCodes;

@Component
@Slf4j
public class PreProcessingSecondJobFetcher implements JobRetriever {
    private static final Gson gson = new Gson();

    @Autowired
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private RAFileDetailsService raFileDetailsService;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PreprocessingService preprocessingService;

    @Override
    public List<Task> refillQueue(Integer tasks) {
        List<RAFileDetails> raFileDetailsList = raFileDetailsService.
                findFileDetailsByStatusCodes(preProcessingJob2StatusCodes, Math.max(10, tasks), 0);
        log.info("raFileDetailsList {} tasks {}", raFileDetailsList.size(), tasks);
        List<Task> executors = new ArrayList<>();
        for (RAFileDetails raFileDetails : raFileDetailsList) {
            if (!preprocessingService.checkCompatibleOrNotAndUpdateFileStatus(raFileDetails.getId())) {
                log.info("Ignoring raFileDetails {}", raFileDetails);
                continue;
            }
            log.info("Picked raFileDetails {}", raFileDetails);
            Map<String, Object> taskData = new HashMap<>();
//            taskData.put("id", raFileMetaData.getFileName());
            taskData.put("data", raFileDetails.getId());
            PreProcessingSecondJobTask preProcessingSecondJobTask = new PreProcessingSecondJobTask(taskData);
            preProcessingSecondJobTask.setApplicationContext(applicationContext);
            executors.add(preProcessingSecondJobTask);
        }
        return executors;
    }

}

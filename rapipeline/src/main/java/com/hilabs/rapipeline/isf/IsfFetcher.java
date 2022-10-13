package com.hilabs.rapipeline.isf;

import com.google.gson.Gson;
import com.hilabs.mcheck.model.JobRetriever;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.*;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
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
    private RAFileStatusUpdatingService raFileStatusUpdatingService;

    @Autowired
    private RASheetDetailsService raSheetDetailsService;

    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private IsfTaskService isfTaskService;

    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;
    @Autowired
    private ApplicationContext applicationContext;

//    27, 31
    @Override
    public List<Task> refillQueue(Integer tasks) {
        try {
            List<RAFileDetails> raFileDetailsList = isfTaskService.getEligibleRAFileDetailsList(Math.max(tasks * 2, 50));
            log.info("raFileDetailsList size {}", raFileDetailsList.size());
            List<Task> executors = new ArrayList<>();
            int count = 0;
            for (RAFileDetails raFileDetails : raFileDetailsList) {
                Long raFileDetailsId = raFileDetails.getId();
                List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
                boolean isCompatible = raFileStatusUpdatingService.checkCompatibleOrNotAndUpdateFileStatus(raFileDetailsId, raSheetDetailsList);
                if (!isCompatible) {
                    log.error("raFileDetails is not eligible for {}", raFileDetails);
                    continue;
                }
                log.error("Picked raFileDetails is not eligible for {}", raFileDetails);
                if (raFileDetails.getStatusCode() == 27) {
                    raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 31);
                }
                for (RASheetDetails raSheetDetails : raSheetDetailsList) {
                    Long raSheetDetailsId = raSheetDetails.getId();
                    if (!isfTaskService.shouldRun(raSheetDetails, true)) {
                        continue;
                    }
                    count++;
                    Map<String, Object> taskData = new HashMap<>();
                    taskData.put("data", raSheetDetails);
                    IsfTask isfTask = new IsfTask(taskData);
                    isfTask.setApplicationContext(applicationContext);
                    executors.add(isfTask);
                    raSheetDetailsService.updateRASheetDetailsStatus(raSheetDetailsId, 150);
                    if (count >= tasks) {
                        break;
                    }
                }
            }
            return executors;
        } catch (Exception ex) {
            log.error("Error IsfFetcher {}", ex.getMessage());
            throw ex;
        }
    }
}

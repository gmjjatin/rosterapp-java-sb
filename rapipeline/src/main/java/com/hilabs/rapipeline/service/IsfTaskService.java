package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.*;

@Service
@Slf4j
public class IsfTaskService {
    private static Gson gson = new Gson();
    @Autowired
    private RAFileDetailsService raFileDetailsService;

    @Autowired
    private RASheetDetailsService raSheetDetailsService;

    @Autowired
    private RAFileStatusUpdatingService raFileStatusUpdatingService;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> isfTaskRunningMap = new ConcurrentHashMap<>();

    public boolean shouldRun(RASheetDetails raSheetDetails, boolean isFetcher) {
        if (isfTaskRunningMap.containsKey(raSheetDetails.getId())) {
            log.warn("IsfTask task in progress for raSheetDetails {}", raSheetDetails);
            return false;
        }
        return isSheetIdEligibleForIsfTask(raSheetDetails, isFetcher);
    }

    public boolean isSheetIdEligibleForIsfTask(RASheetDetails raSheetDetails, boolean isFetcher) {
        if (raSheetDetails.getStatusCode() == null) {
            return false;
        }
        if (isFetcher) {
            if (!Arrays.asList(145).stream().anyMatch(p -> raSheetDetails.getStatusCode().equals(p))) {
                return false;
            }
        } else {
            if (!Arrays.asList(145, 150).stream().anyMatch(p -> raSheetDetails.getStatusCode().equals(p))) {
                return false;
            }
        }
        return true;
    }

    public List<RAFileDetails> getEligibleRAFileDetailsList(int count) {
        //TODO fix it.
        List<RAFileDetails> eligibleRaFileDetailsList = raFileDetailsService.findFileDetailsByStatusCodesWithManualActionReqList(isfFileStatusCodes,
                Collections.singletonList(0), count, 0);
//        List<RAFileDetails> eligibleRaFileDetailsList = new ArrayList<>();
//        for (RAFileDetails raFileDetails : raFileDetailsList) {
//            if (raFileDetails.getManualActionRequired() != null && raFileDetails.getManualActionRequired() == 0) {
//                eligibleRaFileDetailsList.add(raFileDetails);
//            }
//        }
        return eligibleRaFileDetailsList;
    }

    public void invokePythonProcessForIsfTask(RASheetDetails raSheetDetails) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getIsfWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(),"--envConfigs", appPropertiesConfig.getEnvConfigs(),
                    "--sheetDetailsId",  "" + raSheetDetails.getId());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

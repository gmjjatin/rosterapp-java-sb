package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.hasIntersection;
import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.isSubset;
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
    private RASheetDetailsRepository raSheetDetailsRepository;

    @Autowired
    private RAFileStatusUpdatingService raFileStatusUpdatingService;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> isfTaskRunningMap = new ConcurrentHashMap<>();

//    public boolean shouldRun(RASheetDetails raSheetDetails, boolean isFetcher) {
//        if (isfTaskRunningMap.containsKey(raSheetDetails.getId())) {
//            //Just logging
//            log.warn("IsfTask task in progress for raSheetDetails {}", raSheetDetails);
////            return false;
//        }
//        return isSheetIdEligibleForIsfTask(raSheetDetails, isFetcher);
//    }

//    public boolean isSheetIdEligibleForIsfTask(RASheetDetails raSheetDetails, boolean isFetcher) {
//        if (raSheetDetails.getStatusCode() == null) {
//            return false;
//        }
//        if (isFetcher) {
//            if (!Arrays.asList(145).stream().anyMatch(p -> raSheetDetails.getStatusCode().equals(p))) {
//                return false;
//            }
//        } else {
//            if (!Arrays.asList(145, 150).stream().anyMatch(p -> raSheetDetails.getStatusCode().equals(p))) {
//                return false;
//            }
//        }
//        return true;
//    }

    public List<RASheetDetails> getEligibleRAFileDetailsListAndUpdate(int count) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsBasedFileStatusAndSheetStatusCodesForUpdate(isfFileStatusCodes,
                Collections.singletonList(145), Collections.singletonList(0), count);
        List<Long> raSheetDetailsIds = raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        raSheetDetailsRepository.updateRASheetDetailsStatusByIds(raSheetDetailsIds, 150, "SYSTEM", new Date());
        return raSheetDetailsList;
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

    public boolean consolidateISF(Long raFileDetailsId) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
        log.info("consolidateISF for raFileDetailsId {} raSheetDetailsList {}", raFileDetailsId,
                new Gson().toJson(raSheetDetailsList.stream().map(p -> p.getId())));
        List<Integer> sheetCodes = raSheetDetailsList.stream().map(s -> s.getStatusCode()).collect(Collectors.toList());
        if (sheetCodes.stream().anyMatch(Objects::isNull)) {
            log.error("One of the status codes is null for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return false;
        }
        if (sheetCodes.size() == 0) {
            log.error("Zero status codes for raFileDetailsId {}", raFileDetailsId);
            //TODO
            return false;
        }
        //153 - ISF Conversion Failed
        //111 - Roster Sheet Processing not Required
        //119 - Roster Sheet Need to be Processed Manually
        //131 - Post Column Mapping Normalization processing Not required
        //139 - Post Column Mapping Normalization Manual action
        //155 - ISF Conversion Completed
        //145 - Post Column Mapping Normalization completed
        //35 - Roster ISF Generation Completed
        //33 - Roster ISF Generation Failed
        if (hasIntersection(Arrays.asList(153), sheetCodes)) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 33);
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139, 155))) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 35);
            return false;
        }
        return true;
    }
}

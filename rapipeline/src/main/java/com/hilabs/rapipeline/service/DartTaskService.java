package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.model.FileMetaDataTableStatus.FAILED;
import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.hasIntersection;
import static com.hilabs.rapipeline.service.RAFileStatusUpdatingService.isSubset;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.*;

@Service
@Slf4j
public class DartTaskService {
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
    private RAFileMetaDataDetailsService raFileMetaDataDetailsService;

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public static ConcurrentHashMap<Long, Boolean> dartTaskRunningMap = new ConcurrentHashMap<>();

    public List<RASheetDetails> getEligibleRAFileDetailsList(int count) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsBasedFileStatusAndSheetStatusCodesForUpdate(dartStatusCodes,
                Collections.singletonList(155), Collections.singletonList(0), count);
        List<Long> raSheetDetailsIds = raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        raSheetDetailsRepository.updateRASheetDetailsStatusByIds(raSheetDetailsIds, 160, "SYSTEM", new Date());
        return raSheetDetailsList;
    }

    public void invokePythonProcessForDartTask(RASheetDetails raSheetDetails) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getDartWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(),"--envConfigs", appPropertiesConfig.getEnvConfigs(),
                    "--sheetDetailsId",  "" + raSheetDetails.getId());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }

    public boolean consolidateDart(Long raFileDetailsId) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
        log.info("consolidateDart for raFileDetailsId {} raSheetDetailsList {}", raFileDetailsId,
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
        //163 - Dart Generation failed
        //165 - Dart Generated
        //49 - Dart generation completed with validation failure
        if (hasIntersection(Arrays.asList(163), sheetCodes)) {
            raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 43);
            try {
                raFileMetaDataDetailsService.updatePlmStatusForFileDetailsId(raFileDetailsId, FAILED);
            } catch (Exception ex) {
                log.error("Error in updatePlmStatusForFileDetailsId with failed status for raFileDetailsId {}", raFileDetailsId);
            }
            return false;
        } else if (isSubset(sheetCodes, Arrays.asList(111, 119, 131, 139, 165, 157, 167))) {
            if (!hasIntersection(Collections.singletonList(165), sheetCodes)) {
                raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 49);
                raFileMetaDataDetailsService.updatePlmStatusForFileDetailsId(raFileDetailsId, FAILED);
            } else {
                raFileDetailsService.updateRAFileDetailsStatus(raFileDetailsId, 45);
            }
            return false;
        }
        return true;
    }
}
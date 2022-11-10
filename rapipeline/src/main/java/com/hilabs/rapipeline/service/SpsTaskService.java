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

import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.*;

@Service
@Slf4j
public class SpsTaskService {
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

    public static ConcurrentHashMap<Long, Boolean> spsTaskRunningMap = new ConcurrentHashMap<>();

    public List<RASheetDetails> getEligibleRASheetDetailsListAndUpdate(int count) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsBasedOnSheetStatusCodesForUpdate(Arrays.asList(readyForSpsSheetStatusCode), count);
        List<Long> raSheetDetailsIds = raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        raSheetDetailsRepository.updateRASheetDetailsStatusByIds(raSheetDetailsIds,
                spsInQueueSheetStatusCode, "SYSTEM", new Date());
        return raSheetDetailsList;
    }

    public void invokePythonProcessForSpsTask(RASheetDetails raSheetDetails) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getSpsWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(),"--envConfigs", appPropertiesConfig.getEnvConfigs(),
                    "--sheetDetailsId",  "" + raSheetDetails.getId());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.rapipeline.model.DartStatusCheckResponse;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hilabs.rapipeline.service.FileSystemUtilService.downloadUsingNIO;
import static com.hilabs.rapipeline.util.PipelineStatusCodeUtil.*;

@Service
@Slf4j
public class DartUITaskService {
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

    @Autowired
    private RestTemplate restTemplate;

    public static ConcurrentHashMap<Long, Boolean> dartUITaskRunningMap = new ConcurrentHashMap<>();

    public List<RASheetDetails> getEligibleRASheetDetailsListAndUpdate(int count) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.getSheetDetailsBasedOnSheetStatusCodesForUpdate(Collections.singletonList(dartUISheetStatusCode),
                count);
        List<Long> raSheetDetailsIds = raSheetDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList());
        //TODO demo
        raSheetDetailsRepository.updateRASheetDetailsStatusByIds(raSheetDetailsIds, dartUISheetInQueueStatusCode,
                "SYSTEM", new Date());
        return raSheetDetailsList;
    }

    public void invokePythonProcessForDartUITask(RASheetDetails raSheetDetails) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getDartUIWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(),"--envConfigs", appPropertiesConfig.getEnvConfigs(),
                    "--sheetDetailsId",  "" + raSheetDetails.getId());
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }

    //TODO demo
    public DartStatusCheckResponse checkDartUIStatusOfSheet(RASheetDetails raSheetDetails) {
        ResponseEntity<Map> response = restTemplate.getForEntity("https://reqres.in/api/users/2",
                Map.class);
        Map<String, Object> map = (Map<String, Object>) response.getBody();
        log.info(gson.toJson(map.get("data")));
        return new DartStatusCheckResponse("completed");
    }

    public void downloadDartUIResponseFile(RASheetDetails raSheetDetails) throws IOException  {
        downloadUsingNIO("https://file-examples.com/wp-content/uploads/2017/02/file_example_XLS_10.xls", "sample.xls");
    }
}

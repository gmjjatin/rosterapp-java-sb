package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PythonInvocationService {
    private static Gson gson = new Gson();
    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;

    public List<String> invokePythonProcess(String filePath, String ...arguments) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
//            File file = new File(appPropertiesConfig.getPyScriptsFolder(), fileName);
            commands.add(appPropertiesConfig.getPythonCommand());
            commands.add(filePath);
            commands.add("--envConfigs");
            commands.add(appPropertiesConfig.getEnvConfigs());
            commands.addAll(Arrays.asList(arguments));
            log.info("Running command {}", commands.stream().collect(Collectors.joining(" ")));
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            List<String> outputLines = new ArrayList<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.info(line);
                outputLines.add(line);
            }
            return outputLines;
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

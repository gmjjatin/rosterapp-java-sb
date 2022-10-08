package com.hilabs.rapipeline.service;

import com.google.gson.Gson;
import com.hilabs.rapipeline.config.AppPropertiesConfig;
import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Service
public class PythonInvocationService {
    private static Gson gson = new Gson();
    @Autowired
    private AppPropertiesConfig appPropertiesConfig;

    public void invokePythonProcessForPreProcessing(Long raFileDetailsId) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
            File file = new File(appPropertiesConfig.getPreProcessPythonFilePath());
            invokePythonProcess(file.getPath(), String.valueOf(raFileDetailsId));
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }

    public List<String> invokePythonProcess(String filePath, String ...arguments) throws Exception {
        List<String> commands = new ArrayList<>();
        try {
//            File file = new File(appPropertiesConfig.getPyScriptsFolder(), fileName);
            commands.add("python");
            commands.add(filePath);
            commands.addAll(Arrays.asList(arguments));
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            List<String> outputLines = new ArrayList<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                outputLines.add(line);
            }
            return outputLines;
        } catch (Exception ex) {
            log.info("Error in invokePythonProcess - commands {}", gson.toJson(commands));
            throw ex;
        }
    }
}

package com.hilabs.rostertracker.service;

import com.google.gson.Gson;
import com.hilabs.rostertracker.config.ApplicationConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PythonInvocationService {
    public static Gson gson = new Gson();
    @Autowired
    private ApplicationConfig applicationConfig;

    public List<String> invokePythonProcess(String filePath, String ...arguments) throws IOException {
        List<String> commands = new ArrayList<>();
        try {
            commands.add(applicationConfig.getPythonCommand());
            commands.add(filePath);
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

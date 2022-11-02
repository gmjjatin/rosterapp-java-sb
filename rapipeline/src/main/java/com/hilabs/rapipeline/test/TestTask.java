package com.hilabs.rapipeline.test;


import com.google.gson.Gson;
import com.hilabs.mcheck.model.Task;
import com.hilabs.rapipeline.service.PreProcessingTaskService;
import com.hilabs.roster.service.DartRASystemErrorsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TestTask extends Task {

    private PreProcessingTaskService preProcessingTaskService;

    private DartRASystemErrorsService dartRASystemErrorsService;

    private static final Gson gson = new Gson();

    public TestTask(Map<String, Object> taskData) {
        super(taskData);
    }

    @Override
    public void run() {
        boolean isLongTask = System.currentTimeMillis() % 2 == 0;
        log.info("TestTask stared for {} isLongTask {}", gson.toJson(getTaskData()), isLongTask);
        try {
            List<String> commands = new ArrayList<>();
            commands.add("python");
            commands.add("./test.py");
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
            reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = reader.readLine()) != null) {
                log.info(line);
                outputLines.add(line);
            }
           log.info("TestTask ended for {} isLongTask {}", gson.toJson(getTaskData()), isLongTask);
        } catch (Exception | Error ex) {
            log.error("Error in TestTask done for {} message {} stackTrace {}", gson.toJson(getTaskData()),
                    ex.getMessage(), ExceptionUtils.getStackTrace(ex));
        }
    }
}

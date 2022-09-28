package com.rapipeline.executor;

import com.hilabs.mcheck.model.Task;

import java.util.Map;

public class SimpleTask extends Task {

    public SimpleTask(Map<String, Object> taskData) {
        super(taskData);
    }

    @Override
    public void run() {
        getTaskData();
    }
}

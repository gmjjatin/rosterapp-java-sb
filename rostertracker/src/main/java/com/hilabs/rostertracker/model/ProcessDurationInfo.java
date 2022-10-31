package com.hilabs.rostertracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessDurationInfo {
    private long startTime;
    private long endTime;
    private long timeTakenInMillis;
}
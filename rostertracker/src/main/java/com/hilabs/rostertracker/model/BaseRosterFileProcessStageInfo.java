package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.Data;

@Data
public class BaseRosterFileProcessStageInfo {
    private int noOfRecords;
    private Long startTime;
    private RosterSheetProcessStage fileProcessStage;
    private RosterStageState state;

    private long timeTakenInMillis;

    public BaseRosterFileProcessStageInfo() {}

    public BaseRosterFileProcessStageInfo(RosterSheetProcessStage fileProcessStage, int noOfRecords, Long startTime, RosterStageState state, long timeTakenInMillis) {
        this.fileProcessStage = fileProcessStage;
        this.noOfRecords = noOfRecords;
        this.startTime = startTime;
        this.state = state;
        this.timeTakenInMillis = timeTakenInMillis;
    }

    public BaseRosterFileProcessStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        this.fileProcessStage = baseRosterFileProcessStageInfo.fileProcessStage;
        this.noOfRecords = baseRosterFileProcessStageInfo.noOfRecords;
        this.startTime = baseRosterFileProcessStageInfo.getStartTime();
        this.state = baseRosterFileProcessStageInfo.state;
        this.timeTakenInMillis = baseRosterFileProcessStageInfo.timeTakenInMillis;
    }
}

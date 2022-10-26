package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaseRosterFileProcessStageInfo {
    private int noOfRecords;
    private Long startTime;
    private RosterSheetProcessStage fileProcessStage;
    private RosterStageState state;

    private long timeTakenInMillis;

    private List<FalloutReportElement> falloutReport;

    public BaseRosterFileProcessStageInfo() {}

    //TODO demo
    public BaseRosterFileProcessStageInfo(RosterSheetProcessStage fileProcessStage) {
        this.fileProcessStage = fileProcessStage;
        this.noOfRecords = -1;
        this.startTime = -1L;
        this.state = RosterStageState.NOT_STARTED;
        this.timeTakenInMillis = -1;
        this.falloutReport = new ArrayList<>();
    }

    public BaseRosterFileProcessStageInfo(RosterSheetProcessStage fileProcessStage, int noOfRecords, Long startTime,
                                          RosterStageState state, long timeTakenInMillis, List<FalloutReportElement> falloutReport) {
        this.fileProcessStage = fileProcessStage;
        this.noOfRecords = noOfRecords;
        this.startTime = startTime;
        this.state = state;
        this.timeTakenInMillis = timeTakenInMillis;
        this.falloutReport = falloutReport;
    }

    public BaseRosterFileProcessStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        this.fileProcessStage = baseRosterFileProcessStageInfo.fileProcessStage;
        this.noOfRecords = baseRosterFileProcessStageInfo.noOfRecords;
        this.startTime = baseRosterFileProcessStageInfo.getStartTime();
        this.state = baseRosterFileProcessStageInfo.state;
        this.timeTakenInMillis = baseRosterFileProcessStageInfo.timeTakenInMillis;
        this.falloutReport = baseRosterFileProcessStageInfo.falloutReport;
    }
}

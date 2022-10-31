package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BaseRosterFileProcessStageInfo {
    private RosterSheetProcessStage fileProcessStage;
    private RosterStageState state;

    private int noOfRecords;

    private long timeTakenInMillis;
    private long endTime;

    private List<FalloutReportElement> falloutReport;

    public BaseRosterFileProcessStageInfo() {}

    public BaseRosterFileProcessStageInfo(RosterSheetProcessStage rosterSheetProcessStage) {
        this.fileProcessStage = rosterSheetProcessStage;
        this.state = RosterStageState.NOT_STARTED;
        this.noOfRecords = -1;
        this.falloutReport = new ArrayList<>();
        this.endTime = -1;
    }

    public BaseRosterFileProcessStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        this.fileProcessStage = baseRosterFileProcessStageInfo.fileProcessStage;
        this.state = baseRosterFileProcessStageInfo.state;
        this.noOfRecords = baseRosterFileProcessStageInfo.getNoOfRecords();
        this.timeTakenInMillis = baseRosterFileProcessStageInfo.timeTakenInMillis;
        this.falloutReport = baseRosterFileProcessStageInfo.getFalloutReport();
        this.endTime = baseRosterFileProcessStageInfo.endTime;
    }
}
package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseRosterFileProcessStageInfo {
    private RosterSheetProcessStage fileProcessStage;
    private RosterStageState state;

    private int noOfRecords;

    private long timeTakenInMillis;

    public BaseRosterFileProcessStageInfo(RosterSheetProcessStage rosterSheetProcessStage) {
        this.fileProcessStage = rosterSheetProcessStage;
        this.state = RosterStageState.NOT_STARTED;
        this.noOfRecords = -1;
    }

    public BaseRosterFileProcessStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        this.fileProcessStage = baseRosterFileProcessStageInfo.fileProcessStage;
        this.state = baseRosterFileProcessStageInfo.state;
        this.noOfRecords = baseRosterFileProcessStageInfo.getNoOfRecords();
        this.timeTakenInMillis = baseRosterFileProcessStageInfo.timeTakenInMillis;
    }
}
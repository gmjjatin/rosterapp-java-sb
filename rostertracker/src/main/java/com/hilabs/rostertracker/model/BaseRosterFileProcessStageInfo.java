package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.Data;

@Data
public class BaseRosterFileProcessStageInfo {
    private RosterSheetProcessStage fileProcessStage;
    private RosterStageState state;

    private long timeTakenInMillis;

    public BaseRosterFileProcessStageInfo() {}

    public BaseRosterFileProcessStageInfo(RosterSheetProcessStage fileProcessStage, RosterStageState state, long timeTakenInMillis) {
        this.fileProcessStage = fileProcessStage;
        this.state = state;
        this.timeTakenInMillis = timeTakenInMillis;
    }

    public BaseRosterFileProcessStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        this.fileProcessStage = baseRosterFileProcessStageInfo.fileProcessStage;
        this.state = baseRosterFileProcessStageInfo.state;
        this.timeTakenInMillis = baseRosterFileProcessStageInfo.timeTakenInMillis;
    }
}

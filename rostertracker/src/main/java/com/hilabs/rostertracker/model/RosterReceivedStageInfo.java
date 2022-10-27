package com.hilabs.rostertracker.model;


import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;

public class RosterReceivedStageInfo extends BaseRosterFileProcessStageInfo {
    public RosterReceivedStageInfo() {
        super(RosterSheetProcessStage.ROSTER_RECEIVED);
    }

    public RosterReceivedStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}

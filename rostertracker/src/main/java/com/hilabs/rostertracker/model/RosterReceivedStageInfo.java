package com.hilabs.rostertracker.model;


import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;

public class RosterReceivedStageInfo extends BaseRosterFileProcessStageInfo {
    public RosterReceivedStageInfo(long receivedTime) {
        super(RosterSheetProcessStage.ROSTER_RECEIVED, -1, receivedTime, RosterStageState.COMPLETED, receivedTime);
    }

    public RosterReceivedStageInfo(long receivedTime, int noOfRecords) {
        super(RosterSheetProcessStage.ROSTER_RECEIVED, noOfRecords, receivedTime, RosterStageState.COMPLETED, receivedTime);
    }
}

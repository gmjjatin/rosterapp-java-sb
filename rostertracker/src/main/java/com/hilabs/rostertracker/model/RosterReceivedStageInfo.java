package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

public class RosterReceivedStageInfo extends BaseRosterFileProcessStageInfo {

    public RosterReceivedStageInfo() {
        super(RosterSheetProcessStage.AUTO_MAPPED);
    }

    public RosterReceivedStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }

    public RosterReceivedStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}
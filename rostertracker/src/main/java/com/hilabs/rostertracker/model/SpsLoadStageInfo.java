package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class SpsLoadStageInfo extends RosterFileProcessIntermediateStageInfo {
    public SpsLoadStageInfo() {
        super(RosterSheetProcessStage.SPS_LOAD);
    }

    public SpsLoadStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo, processingThresholdInMillis);
    }

    public SpsLoadStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
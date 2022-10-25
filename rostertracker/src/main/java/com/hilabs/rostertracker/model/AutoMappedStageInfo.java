package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class AutoMappedStageInfo extends RosterFileProcessIntermediateStageInfo {

    public AutoMappedStageInfo() {
        super(RosterSheetProcessStage.AUTO_MAPPED);
    }

    public AutoMappedStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }

    public AutoMappedStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo, processingThresholdInMillis);
    }
}
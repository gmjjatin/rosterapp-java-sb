package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

import java.util.List;

public class SpsLoadStageInfo extends RosterFileProcessIntermediateStageInfo {
    public SpsLoadStageInfo() {
        super(RosterSheetProcessStage.SPS_LOAD);
    }

    public SpsLoadStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo,
                            long processingThresholdInMillis, List<FalloutReportElement> falloutReport) {
        super(baseRosterFileProcessStageInfo, processingThresholdInMillis, falloutReport);
    }

    public SpsLoadStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
package com.anthem.rostertracker.model;

import lombok.Data;

@Data
public class SpsLoadStageInfo extends RosterFileProcessIntermediateStageInfo {
    public SpsLoadStageInfo() {
        super(RosterFileProcessStage.SPS_LOAD);
    }

    public SpsLoadStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo, noOfRecords, processingThresholdInMillis);
    }

    public SpsLoadStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
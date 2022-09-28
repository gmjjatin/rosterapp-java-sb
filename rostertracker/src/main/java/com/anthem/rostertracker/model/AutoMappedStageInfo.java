package com.anthem.rostertracker.model;

import lombok.Data;

@Data
public class AutoMappedStageInfo extends RosterFileProcessIntermediateStageInfo {

    public AutoMappedStageInfo() {
        super(RosterFileProcessStage.AUTO_MAPPED);
    }

    public AutoMappedStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }

    public AutoMappedStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo, noOfRecords, processingThresholdInMillis);
    }
}
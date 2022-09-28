package com.anthem.rostertracker.model;

import lombok.Data;

@Data
public class ConvertedDartStageInfo extends RosterFileProcessIntermediateStageInfo {
    public ConvertedDartStageInfo() {
        super(RosterFileProcessStage.CONVERTED_DART);
    }

    public ConvertedDartStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo, noOfRecords, processingThresholdInMillis);
    }

    public ConvertedDartStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
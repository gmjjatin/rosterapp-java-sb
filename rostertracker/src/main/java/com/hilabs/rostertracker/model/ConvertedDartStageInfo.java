package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterFileProcessStage;
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
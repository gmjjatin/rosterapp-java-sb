package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class ConvertedDartStageInfo extends RosterFileProcessIntermediateStageInfo {
    public ConvertedDartStageInfo() {
        super(RosterSheetProcessStage.CONVERTED_DART);
    }

    public ConvertedDartStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo, noOfRecords, processingThresholdInMillis);
    }

    public ConvertedDartStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
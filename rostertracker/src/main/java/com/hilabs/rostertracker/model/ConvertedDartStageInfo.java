package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class ConvertedDartStageInfo extends RosterFileProcessIntermediateStageInfo {
    public ConvertedDartStageInfo() {
        super(RosterSheetProcessStage.CONVERTED_DART);
    }

    public ConvertedDartStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo, processingThresholdInMillis);
    }

    public ConvertedDartStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
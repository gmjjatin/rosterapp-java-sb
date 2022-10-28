package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

import java.util.List;

@Data
public class AutoMappedStageInfo extends RosterFileProcessIntermediateStageInfo {

    public AutoMappedStageInfo() {
        super(RosterSheetProcessStage.AUTO_MAPPED);
    }

    public AutoMappedStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }

    public AutoMappedStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords, long processingThresholdInMillis, List<FalloutReportElement> falloutReport) {
        super(baseRosterFileProcessStageInfo, noOfRecords, processingThresholdInMillis, falloutReport);
    }
}
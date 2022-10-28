package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

import java.util.List;

@Data
public class ISFStageInfo extends RosterFileProcessIntermediateStageInfo {

    public ISFStageInfo() {
        super(RosterSheetProcessStage.ISF_GENERATED);
    }

    public ISFStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }

    public ISFStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords, long processingThresholdInMillis, List<FalloutReportElement> falloutReport) {
        super(baseRosterFileProcessStageInfo, noOfRecords, processingThresholdInMillis, falloutReport);
    }
}
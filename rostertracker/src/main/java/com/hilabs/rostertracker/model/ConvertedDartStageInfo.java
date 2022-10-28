package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

import java.util.List;

@Data
public class ConvertedDartStageInfo extends RosterFileProcessIntermediateStageInfo {
    public ConvertedDartStageInfo() {
        super(RosterSheetProcessStage.CONVERTED_DART);
    }

    public ConvertedDartStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords,
                                  long processingThresholdInMillis, List<FalloutReportElement> falloutReport) {
        super(baseRosterFileProcessStageInfo, noOfRecords, processingThresholdInMillis, falloutReport);
    }

    public ConvertedDartStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
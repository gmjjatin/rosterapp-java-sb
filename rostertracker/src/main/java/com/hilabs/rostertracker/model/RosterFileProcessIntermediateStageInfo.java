package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RosterFileProcessIntermediateStageInfo extends BaseRosterFileProcessStageInfo {

    private long processThresholdInMillis;

    private List<FalloutReportElement> falloutReport;

    public RosterFileProcessIntermediateStageInfo(RosterSheetProcessStage rosterFileProcessStage) {
        super(rosterFileProcessStage, RosterStageState.NOT_STARTED, -1, 0);
        this.processThresholdInMillis = -1;
        this.falloutReport = new ArrayList<>();
    }

    public RosterFileProcessIntermediateStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, long processingThresholdInMillis, List<FalloutReportElement> falloutReport) {
        super(baseRosterFileProcessStageInfo);
        this.processThresholdInMillis = processingThresholdInMillis;
        this.falloutReport = falloutReport;
    }

    public RosterFileProcessIntermediateStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
        this.processThresholdInMillis = rosterFileProcessIntermediateStageInfo.processThresholdInMillis;
        this.falloutReport = rosterFileProcessIntermediateStageInfo.falloutReport;
    }
}
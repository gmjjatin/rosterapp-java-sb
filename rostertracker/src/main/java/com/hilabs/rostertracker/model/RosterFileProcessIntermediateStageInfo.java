package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import com.hilabs.rostertracker.dto.ErrorSummaryElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RosterFileProcessIntermediateStageInfo extends BaseRosterFileProcessStageInfo {

    private long processThresholdInMillis;

    private List<FalloutReportElement> falloutReport;

    public RosterFileProcessIntermediateStageInfo() {
    }

    public RosterFileProcessIntermediateStageInfo(RosterSheetProcessStage rosterFileProcessStage) {
        super(rosterFileProcessStage, -1, -1L, RosterStageState.NOT_STARTED, 0);
        this.processThresholdInMillis = -1;
        this.falloutReport = new ArrayList<>();
    }

    public RosterFileProcessIntermediateStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo);
        this.processThresholdInMillis = processingThresholdInMillis;
    }

    public RosterFileProcessIntermediateStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
        this.processThresholdInMillis = rosterFileProcessIntermediateStageInfo.processThresholdInMillis;
    }
}
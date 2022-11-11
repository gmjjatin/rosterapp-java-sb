package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
public class RosterFileProcessIntermediateStageInfo extends BaseRosterFileProcessStageInfo {

    private long processThresholdInMillis;
    private boolean hasFallouts;

    public RosterFileProcessIntermediateStageInfo(RosterSheetProcessStage rosterFileProcessStage) {
        super(rosterFileProcessStage, RosterStageState.NOT_STARTED, -1, 0, -1, new ArrayList<>());
        this.processThresholdInMillis = -1;
        this.hasFallouts = false;
    }

    public RosterFileProcessIntermediateStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, long processingThresholdInMillis, boolean hasFallouts) {
        super(baseRosterFileProcessStageInfo);
        this.processThresholdInMillis = processingThresholdInMillis;
        this.hasFallouts = hasFallouts;
    }

    public RosterFileProcessIntermediateStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
        this.processThresholdInMillis = rosterFileProcessIntermediateStageInfo.processThresholdInMillis;
        this.hasFallouts = rosterFileProcessIntermediateStageInfo.hasFallouts;
    }
}
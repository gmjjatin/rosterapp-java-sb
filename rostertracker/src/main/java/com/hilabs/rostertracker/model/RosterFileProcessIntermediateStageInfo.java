package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterFileProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.Data;

@Data
public class RosterFileProcessIntermediateStageInfo extends BaseRosterFileProcessStageInfo {
    private int noOfRecords;

    private long processThresholdInMillis;

    public RosterFileProcessIntermediateStageInfo() {
    }

    public RosterFileProcessIntermediateStageInfo(RosterFileProcessStage rosterFileProcessStage) {
        super(rosterFileProcessStage, RosterStageState.NOT_STARTED, 0);
        this.noOfRecords = -1;
        this.processThresholdInMillis = -1;
    }

    public RosterFileProcessIntermediateStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo, int noOfRecords, long processingThresholdInMillis) {
        super(baseRosterFileProcessStageInfo);
        this.noOfRecords = noOfRecords;
        this.processThresholdInMillis = processingThresholdInMillis;
    }

    public RosterFileProcessIntermediateStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
        this.noOfRecords = rosterFileProcessIntermediateStageInfo.noOfRecords;
        this.processThresholdInMillis = rosterFileProcessIntermediateStageInfo.processThresholdInMillis;
    }
}
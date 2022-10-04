package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterFileProcessStage;
import com.hilabs.roster.model.RosterStageState;
import lombok.Data;

@Data
public class ReportStageInfo extends BaseRosterFileProcessStageInfo {
    public ReportStageInfo() {
        super(RosterFileProcessStage.REPORT, RosterStageState.NOT_STARTED, 0);
    }

    public ReportStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}
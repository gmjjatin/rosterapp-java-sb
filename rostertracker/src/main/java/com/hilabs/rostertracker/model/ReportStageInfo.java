package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterFileProcessStage;
import lombok.Data;

@Data
public class ReportStageInfo extends BaseRosterFileProcessStageInfo {
    public ReportStageInfo() {
        super(RosterFileProcessStage.REPORT, RosterFileStageState.NOT_STARTED, 0);
    }

    public ReportStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}
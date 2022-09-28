package com.anthem.rostertracker.model;

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
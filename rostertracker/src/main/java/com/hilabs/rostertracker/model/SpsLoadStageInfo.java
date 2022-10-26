package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class SpsLoadStageInfo extends BaseRosterFileProcessStageInfo {
    public SpsLoadStageInfo() {
        super(RosterSheetProcessStage.SPS_LOAD);
    }

    public SpsLoadStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}
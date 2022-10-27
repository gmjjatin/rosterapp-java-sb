package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class AutoMappedStageInfo extends BaseRosterFileProcessStageInfo {

    public AutoMappedStageInfo() {
        super(RosterSheetProcessStage.AUTO_MAPPED);
    }

    public AutoMappedStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}
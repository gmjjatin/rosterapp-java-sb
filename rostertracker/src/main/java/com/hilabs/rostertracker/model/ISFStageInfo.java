package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class ISFStageInfo extends BaseRosterFileProcessStageInfo {

    public ISFStageInfo() {
        super(RosterSheetProcessStage.ISF_GENERATED);
    }

    public ISFStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}
package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

import java.util.List;
public class ISFStageInfo extends RosterFileProcessIntermediateStageInfo {

    public ISFStageInfo() {
        super(RosterSheetProcessStage.ISF_GENERATED);
    }

    public ISFStageInfo(RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo) {
        super(rosterFileProcessIntermediateStageInfo);
    }
}
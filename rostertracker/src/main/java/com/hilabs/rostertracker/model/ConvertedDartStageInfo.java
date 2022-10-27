package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.Data;

@Data
public class ConvertedDartStageInfo extends BaseRosterFileProcessStageInfo {
    public ConvertedDartStageInfo() {
        super(RosterSheetProcessStage.CONVERTED_DART);
    }

    public ConvertedDartStageInfo(BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo) {
        super(baseRosterFileProcessStageInfo);
    }
}
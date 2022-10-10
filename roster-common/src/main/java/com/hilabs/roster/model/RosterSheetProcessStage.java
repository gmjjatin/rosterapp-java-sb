package com.hilabs.roster.model;

import java.util.Arrays;
import java.util.List;

public enum RosterSheetProcessStage {
    ROSTER_RECEIVED("Roster Received", 0),
//    PRE_PROCESSED("Pre Processed", 1),
    AUTO_MAPPED("Auto Mapped", 2),
    ISF_GENERATED("Auto Mapped", 3),

    CONVERTED_DART("Converted Dart", 4),
    SPS_LOAD("SPS Load", 5),
    REPORT("Report", 6),

    UNKNOWN("Unknown", -1);
    public final String displayName;
    public final int rank;
    RosterSheetProcessStage(String displayName, int rank) {
        this.displayName = displayName;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static List<RosterSheetProcessStage> getValidRosterFileProcessStageListInOrder() {
        return Arrays.asList(ROSTER_RECEIVED, AUTO_MAPPED, CONVERTED_DART, SPS_LOAD, REPORT);
    }
}

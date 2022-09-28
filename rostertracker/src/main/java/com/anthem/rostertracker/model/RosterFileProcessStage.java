package com.anthem.rostertracker.model;

import org.omg.CORBA.UNKNOWN;

import java.util.Arrays;
import java.util.List;

public enum RosterFileProcessStage {
    ROSTER_RECEIVED("Roster Received", 0),
    AUTO_MAPPED("Auto Mapped", 1),
    CONVERTED_DART("Converted Dart", 2),
    SPS_LOAD("SPS Load", 3),
    REPORT("Report", 4),

    UNKNOWN("Unknown", -1);
    public final String displayName;
    public final int rank;
    RosterFileProcessStage(String displayName, int rank) {
        this.displayName = displayName;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static List<RosterFileProcessStage> getValidRosterFileProcessStageListInOrder() {
        return Arrays.asList(ROSTER_RECEIVED, AUTO_MAPPED, CONVERTED_DART, SPS_LOAD, REPORT);
    }
}

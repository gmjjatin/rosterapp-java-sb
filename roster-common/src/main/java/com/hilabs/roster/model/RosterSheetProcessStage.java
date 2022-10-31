package com.hilabs.roster.model;

import java.util.Arrays;
import java.util.List;

public enum RosterSheetProcessStage {
    ROSTER_RECEIVED("Roster Received", 0),
    AUTO_MAPPED("Auto Mapped", 1),
    ISF_GENERATED("ISF GENERATED", 2),

    CONVERTED_DART("Converted Dart", 3),
    SPS_LOAD("SPS Load", 4);
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
        return Arrays.asList(ROSTER_RECEIVED, AUTO_MAPPED, ISF_GENERATED, CONVERTED_DART, SPS_LOAD);
    }

    public static RosterSheetProcessStage getRosterSheetProcessStageFromStr(String type) {
        try {
            return RosterSheetProcessStage.valueOf(type);
        } catch (Exception ignored) {

        }
        //TODO fix it
        if (type.toLowerCase().endsWith("ingestion")) {
            return ROSTER_RECEIVED;
        } else if (type.toLowerCase().endsWith("processing")) {
            return AUTO_MAPPED;
        } else if (type.toLowerCase().startsWith("isf")) {
            return ISF_GENERATED;
        } else if (type.toLowerCase().startsWith("dart") && type.toLowerCase().endsWith("generation")) {
            return CONVERTED_DART;
        } else if (type.toLowerCase().startsWith("dart") && type.toLowerCase().endsWith("ui")) {
            return SPS_LOAD;
        }
        return ROSTER_RECEIVED;
    }
}

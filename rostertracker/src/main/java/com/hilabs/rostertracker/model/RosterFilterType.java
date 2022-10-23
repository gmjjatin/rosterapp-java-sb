package com.hilabs.rostertracker.model;

public enum RosterFilterType {
    ROSTER_TRACKER,
    ERROR_REPORTING,
    CONFIGURATOR,
    NON_COMPATIBLE,
    SYSTEM_ERROR;
    public static RosterFilterType getRosterFilterTypeFromStr(String type) {
        try {
            return RosterFilterType.valueOf(type);
        } catch (Exception ignored) {

        }
        //TODO fix it
        if (type.toLowerCase().endsWith("tracker")) {
            return ROSTER_TRACKER;
        } else if (type.toLowerCase().endsWith("reporting")) {
            return ERROR_REPORTING;
        } else if (type.toLowerCase().startsWith("config")) {
            return CONFIGURATOR;
        } else if (type.toLowerCase().endsWith("compatible")) {
            return NON_COMPATIBLE;
        } else if (type.toLowerCase().startsWith("system")) {
            return SYSTEM_ERROR;
        }
        return ROSTER_TRACKER;
    }
}


package com.hilabs.rostertracker.model;

public enum TargetPhaseType {
    PREPROCESSING_COMPLETED,
    READY_FOR_ISF;
    public static TargetPhaseType getTargetPhaseTypeFromStr(String type) {
        try {
            return TargetPhaseType.valueOf(type);
        } catch (Exception ignored) {

        }
        //TODO fix it
        if (type.toUpperCase().startsWith("PREPROCESSING")) {
            return PREPROCESSING_COMPLETED;
        } else if (type.toUpperCase().endsWith("ISF")) {
            return READY_FOR_ISF;
        }
        return null;
    }
}


package com.hilabs.rostertracker.model;

public enum TargetPhaseType {
    PREPROCESSING_COMPLETED,
    ISF_COMPLETED;
    public static TargetPhaseType getTargetPhaseTypeFromStr(String type) {
        try {
            return TargetPhaseType.valueOf(type);
        } catch (Exception ignored) {

        }
        //TODO fix it
        if (type.toUpperCase().startsWith("PREPROCESSING")) {
            return PREPROCESSING_COMPLETED;
        } else if (type.toUpperCase().startsWith("ISF")) {
            return ISF_COMPLETED;
        }
        return null;
    }
}


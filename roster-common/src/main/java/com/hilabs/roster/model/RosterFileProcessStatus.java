package com.hilabs.roster.model;

//TODO confirm the statuses
public enum RosterFileProcessStatus {
    NOT_YET_STARTED,
    RUNNING_HEADER_MAPPING,
    HEADER_MAPPING_FINISHED,
    READY_FOR_ROSTER_ISF_CONVERSION,
    CONVERTING_ROSTER_ISF,
    READY_FOR_ISF_DART_CONVERSION,
    CONVERTING_ISF_DART,
    READY_FOR_DART_UI,
    PROCESSING_IN_DART_UI,
    READY_FOR_SPS_LOAD,
    PROCESSING_IN_SPS_LOAD,
    READY_FOR_SUMMARY,
    CREATING_SUMMARY,
    SUCCEEDED,
    FAILED
}

package com.hilabs.rostertracker.model;

import lombok.Data;

@Data
public class RosterFileErrorInfo {
    private String errorType;
    private String errorCode;
    private Integer noOfRecords;
    private String description;
    public RosterFileErrorInfo(String errorType, String errorCode, String description) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.description = description;
    }

    public void incrementNoOfRecords(Integer n) {
        if (n == null) {
            return;
        }
        this.noOfRecords += n;
    }
}

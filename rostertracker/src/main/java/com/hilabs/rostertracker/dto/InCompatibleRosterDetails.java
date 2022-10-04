package com.hilabs.rostertracker.dto;

import lombok.Data;

@Data
public class InCompatibleRosterDetails {

    private Long raFileDetailsId;
    private String fileName;
    private Long fileReceivedTime;
    private Integer rosterRecordCount;
    private String error;
    private String errorCode;
    public InCompatibleRosterDetails(Long raFileDetailsId, String fileName, long fileReceivedTime, Integer rosterRecordCount,
                                     String error, String errorCode) {
        this.rosterRecordCount = rosterRecordCount;
        this.raFileDetailsId = raFileDetailsId;
        this.fileName = fileName;
        this.fileReceivedTime = fileReceivedTime;
        this.error = error;
        this.errorCode = errorCode;
    }
}

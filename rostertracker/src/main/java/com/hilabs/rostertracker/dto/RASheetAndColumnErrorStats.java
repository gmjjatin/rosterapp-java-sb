package com.hilabs.rostertracker.dto;


import lombok.Data;

@Data
public class RASheetAndColumnErrorStats {
    private long raSheetDetailsId;
    private String sheetName;

    private Integer statusCode;

    private int columnFalloutCount;

    private int manualReviewRecordCount;

    private int validationFalloutCount;

    private int dartFalloutCount;

    private int spsFalloutCount;


    public RASheetAndColumnErrorStats(long raSheetDetailsId, String sheetName, Integer statusCode) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.statusCode = statusCode;
        //TODO manikanta change names and hardcoding;
        this.columnFalloutCount = 3;
        this.manualReviewRecordCount = 0;
        this.validationFalloutCount = 0;
        this.dartFalloutCount = 0;
        this.spsFalloutCount = 7;
    }
}
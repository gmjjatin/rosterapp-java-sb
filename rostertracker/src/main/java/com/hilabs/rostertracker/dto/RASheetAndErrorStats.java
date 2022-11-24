package com.hilabs.rostertracker.dto;


import lombok.Data;

@Data
public class RASheetAndErrorStats extends RosterErrorStats {
    private long raSheetDetailsId;
    private String sheetName;

    private Integer statusCode;

    public RASheetAndErrorStats(long raSheetDetailsId, String sheetName, Integer statusCode, RASheetAndStats raSheetAndStats) {
        super(raSheetAndStats);
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.statusCode = statusCode;
    }
}
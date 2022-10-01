package com.hilabs.rostertracker.dto;


import lombok.Data;

@Data
public class RASheetAndErrorStats extends RosterErrorStats {
    private long raSheetDetailsId;
    private String sheetName;

    public RASheetAndErrorStats(long raSheetDetailsId, String sheetName, RASheetAndStats raSheetAndStats) {
        super(raSheetAndStats);
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
    }
}
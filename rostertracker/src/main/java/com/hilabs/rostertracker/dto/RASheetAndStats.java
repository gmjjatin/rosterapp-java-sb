package com.hilabs.rostertracker.dto;


import lombok.Data;

@Data
public class RASheetAndStats extends RosterStats {
    private long raSheetDetailsId;
    private String sheetName;
    private boolean isSPSLoadComplete;

    public RASheetAndStats(long raSheetDetailsId, String sheetName, boolean isSPSLoadComplete) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.isSPSLoadComplete = isSPSLoadComplete;
    }
}
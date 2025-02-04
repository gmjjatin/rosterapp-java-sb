package com.hilabs.rostertracker.dto;


import lombok.Data;

@Data
public class RASheetAndStats extends RosterStats {
    private long raSheetDetailsId;
    private String sheetName;

    private String status;

    public RASheetAndStats(long raSheetDetailsId, String sheetName, String status) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.status = status;
    }
}
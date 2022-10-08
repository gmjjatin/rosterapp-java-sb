package com.hilabs.rostertracker.dto;

import lombok.Data;

@Data
public class SheetDetails {
    private Long raSheetDetailsId;
    private String sheetName;
    private String classification;
    private RASheetType raSheetType;

    public SheetDetails(Long raSheetDetailsId, String sheetName, String classification, RASheetType raSheetType) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.classification = classification;
        this.raSheetType = raSheetType;
    }
}

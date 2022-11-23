package com.hilabs.rostertracker.dto;

import lombok.Data;

@Data
public class SheetDetails {
    private Long raSheetDetailsId;
    private String sheetName;
    private String classification;
    private String raSheetType;
    private boolean hasColumnMapping;

    private Integer statusCode;

    public SheetDetails(Long raSheetDetailsId, String sheetName, String classification, String raSheetType, boolean hasColumnMapping, Integer statusCode) {
        this.raSheetDetailsId = raSheetDetailsId;
        this.sheetName = sheetName;
        this.classification = classification;
        this.raSheetType = raSheetType;
        this.hasColumnMapping = hasColumnMapping;
        this.statusCode = statusCode;
    }
}

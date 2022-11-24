package com.hilabs.rostertracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RASheetAndStats extends RosterStats {
    private long raSheetDetailsId;
    private String sheetName;
    private String status;
    private Integer statusCode;
    private String type;
}
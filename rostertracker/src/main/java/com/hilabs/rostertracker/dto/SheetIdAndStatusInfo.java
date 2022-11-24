package com.hilabs.rostertracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SheetIdAndStatusInfo {
    private long raSheetDetailsId;
    private Integer statusCode;
}
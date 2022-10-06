package com.hilabs.rostertracker.controller.model;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateColumnMappingSheetData {
    private Long raSheetDetailsId;
    private Map<String, String> data;
}

package com.hilabs.rostertracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RosterSheetColumnMappingInfo {
    private Long raSheetDetailsId;
    private List<RosterColumnMappingData> data;
}

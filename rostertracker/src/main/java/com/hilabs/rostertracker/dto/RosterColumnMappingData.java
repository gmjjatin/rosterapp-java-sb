package com.hilabs.rostertracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RosterColumnMappingData {
    private String rosterColumnName;
    private List<String> isfColumnValues;
}

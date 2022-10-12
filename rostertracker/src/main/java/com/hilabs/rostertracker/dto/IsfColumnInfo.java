package com.hilabs.rostertracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IsfColumnInfo {
    private String isfColumn;
    private boolean isRecommended;
}

package com.hilabs.rostertracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FalloutReportInfo {
    private List<FalloutReportElement> falloutReportElements;
    private boolean hasFallouts;
}

package com.anthem.rostertracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class RASheetFalloutReport {
    private List<RAFalloutErrorInfo> raFalloutErrorInfoList;
}

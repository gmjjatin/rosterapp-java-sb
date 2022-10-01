package com.hilabs.rostertracker.dto;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import lombok.Data;

import java.util.List;

@Data
public class RASheetFalloutReport {
    private List<RAFalloutErrorInfo> raFalloutErrorInfoList;
}

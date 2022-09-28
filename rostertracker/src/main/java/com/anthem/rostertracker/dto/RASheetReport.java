package com.anthem.rostertracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class RASheetReport {
    private String market;
    private String apdoContact;
    private String peContact;
    private int tablesIdentifiedInRosterSheetCount;
    private int rosterRecordCount;
    private int isfRowCount;
    private int dartRowCount;
    private int spsLoadTransactionCount;
    private int successCount;
    private int warningCount;
    private int failedCount;
    private int spsLoadSuccessTransactionCount;
    //TODO manikanta. confirm???
    private int isfFalloutRecordCount;
    private int brmFalloutRecordCount;
    private List<ErrorDescriptionAndCount> errorDescriptionAndCountList;
}

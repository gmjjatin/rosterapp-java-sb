package com.hilabs.rostertracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class RASheetReport {
    private String market;
    private String apdoContact;
    private String peContact;
    private Integer tablesIdentifiedInRosterSheetCount;
    private Integer rosterRecordCount;
    private Integer isfRowCount;
    private Integer dartRowCount;
    private Integer spsLoadTransactionCount;
    private Integer successCount;
    private Integer warningCount;
    private Integer failedCount;
    private int spsLoadSuccessTransactionCount;
    //TODO manikanta. confirm???
    private Integer isfFalloutRecordCount;
    private Integer brmFalloutRecordCount;
    private List<ErrorDescriptionAndCount> errorDescriptionAndCountList;
}

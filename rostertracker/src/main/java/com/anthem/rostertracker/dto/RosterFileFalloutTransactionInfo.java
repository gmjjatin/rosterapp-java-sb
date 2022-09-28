package com.anthem.rostertracker.dto;

import com.anthem.rostertracker.entity.RosConvFalloutTransactionStatus;
import lombok.Data;

@Data
public class RosterFileFalloutTransactionInfo {
    private String transactionType;
    private String description;
    private int noOfSuccess;
    private int noOfFailure;
    private int noOfWarning;

    public RosterFileFalloutTransactionInfo(String transactionType, String description) {
        this.transactionType = transactionType;
        this.description = description;
    }

    public void increment(RosConvFalloutTransactionStatus rosConvFalloutTransactionStatus, int n) {
        if (rosConvFalloutTransactionStatus == RosConvFalloutTransactionStatus.SUCCESS) {
            noOfSuccess += n;
        } else if (rosConvFalloutTransactionStatus == RosConvFalloutTransactionStatus.FAILURE) {
            noOfFailure += n;
        } else {
            noOfWarning += n;
        }
    }
}

package com.hilabs.rostertracker.dto;

import lombok.Data;

@Data
public class RosterStats {
    private Long rosterRecordCount;
    private Long successfulRecordCount;
    private Long falloutRecordCount;
    private Long manualReviewRecordCount;

    public RosterStats() {}

    public RosterStats(RosterStats rosterStats) {
        this.rosterRecordCount = rosterStats.getRosterRecordCount();
        this.successfulRecordCount = rosterStats.getSuccessfulRecordCount();
        this.falloutRecordCount = rosterStats.getFalloutRecordCount();
        this.manualReviewRecordCount = rosterStats.getManualReviewRecordCount();
    }

    @Override
    public String toString() {
        return "RosterErrorStats";
    }

    public void increment(RosterStats rosterErrorStats) {
        this.rosterRecordCount += rosterErrorStats.rosterRecordCount;
        this.successfulRecordCount += rosterErrorStats.successfulRecordCount;
        this.falloutRecordCount += rosterErrorStats.falloutRecordCount;
        this.manualReviewRecordCount += rosterErrorStats.manualReviewRecordCount;
    }
}

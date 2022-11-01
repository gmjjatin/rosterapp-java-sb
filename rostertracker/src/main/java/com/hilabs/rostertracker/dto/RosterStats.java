package com.hilabs.rostertracker.dto;

import lombok.Data;

@Data
public class RosterStats {
    private Integer rosterRecordCount;
    private Integer successfulRecordCount;
    private Integer falloutRecordCount;
    private Integer manualReviewRecordCount;

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
        if (rosterErrorStats.rosterRecordCount != null) {
            this.rosterRecordCount = this.rosterRecordCount == null ? 0 : this.rosterRecordCount;
            this.rosterRecordCount += rosterErrorStats.rosterRecordCount;
        }

        if (rosterErrorStats.successfulRecordCount != null) {
            this.successfulRecordCount = this.successfulRecordCount == null ? 0 : this.successfulRecordCount;
            this.successfulRecordCount += rosterErrorStats.successfulRecordCount;
        }

        if (rosterErrorStats.falloutRecordCount != null) {
            this.falloutRecordCount = this.falloutRecordCount == null ? 0 : this.falloutRecordCount;
            this.falloutRecordCount += rosterErrorStats.falloutRecordCount;
        }

        if (rosterErrorStats.manualReviewRecordCount != null) {
            this.manualReviewRecordCount = this.manualReviewRecordCount == null ? 0 : this.manualReviewRecordCount;
            this.manualReviewRecordCount += rosterErrorStats.manualReviewRecordCount;
        }
    }
}

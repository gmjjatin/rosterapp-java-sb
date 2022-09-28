package com.anthem.rostertracker.dto;

import lombok.Data;

@Data
public class RosterErrorStats extends RosterStats {

    private int spsLoadTransactionCount;
    private int spsLoadSuccessTransactionCount;
    private int spsLoadFailedTransactionCount;
    //TODO set percentage
    private double spsLoadSuccessTransactionPercent;

    private boolean isSPSLoadComplete;

    public RosterErrorStats() {}

    public RosterErrorStats(RosterStats rosterStats) {
        super(rosterStats);
    }

    @Override
    public String toString() {
        return "RosterErrorStats";
    }

    public void increment(RosterErrorStats rosterErrorStats) {
        super.increment(rosterErrorStats);
        this.spsLoadTransactionCount += rosterErrorStats.spsLoadTransactionCount;
        this.spsLoadSuccessTransactionCount += rosterErrorStats.spsLoadSuccessTransactionCount;
        this.spsLoadFailedTransactionCount += rosterErrorStats.spsLoadFailedTransactionCount;
        double percent = spsLoadTransactionCount > 0 ? (spsLoadSuccessTransactionCount * 100.0 / spsLoadTransactionCount) : 0;
        this.spsLoadSuccessTransactionPercent = Math.round(percent * 100.0) / 100.0;
    }

}

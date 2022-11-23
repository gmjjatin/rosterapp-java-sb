package com.hilabs.rostertracker.dto;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RAFileAndErrorStats extends RosterErrorStats {
    private long raFileDetailsId;
    private String fileName;
    private int noOfSheets;
    private long fileReceivedTime;

    private Integer statusCode;
    private List<RASheetAndErrorStats> sheetStatsList;

    public RAFileAndErrorStats(long raFileDetailsId, String fileName, long fileReceivedTime, Integer statusCode) {
        this.raFileDetailsId = raFileDetailsId;
        this.fileName = fileName;
        this.fileReceivedTime = fileReceivedTime;
        super.setSPSLoadComplete(true);
        this.statusCode = statusCode;
        this.sheetStatsList = new ArrayList<>();
    }

    public void addSheetDetails(RASheetAndErrorStats raSheetAndErrorStats) {
        noOfSheets += 1;
        sheetStatsList.add(raSheetAndErrorStats);
        increment(raSheetAndErrorStats);
        super.setSPSLoadComplete(super.isSPSLoadComplete() & raSheetAndErrorStats.isSPSLoadComplete());
    }
}

package com.hilabs.rostertracker.dto;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RAFileAndStats extends RosterStats {
    private long raFileDetailsId;
    private String fileName;
    private int noOfSheets;
    private long fileReceivedTime;
    private boolean isSPSLoadComplete;
    private List<RASheetAndStats> sheetStatsList;

    public RAFileAndStats(long raFileDetailsId, String fileName, long fileReceivedTime) {
        this.raFileDetailsId = raFileDetailsId;
        this.fileName = fileName;
        this.fileReceivedTime = fileReceivedTime;
        this.isSPSLoadComplete = true;
        this.sheetStatsList = new ArrayList<>();
    }

    public void addSheetDetails(RASheetAndStats raSheetAndStats) {
        noOfSheets += 1;
        sheetStatsList.add(raSheetAndStats);
        increment(raSheetAndStats);
        isSPSLoadComplete = isSPSLoadComplete & raSheetAndStats.isSPSLoadComplete();
    }
}

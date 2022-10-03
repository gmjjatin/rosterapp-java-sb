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

    private String status;
//    private boolean isSPSLoadComplete;
    private List<RASheetAndStats> sheetStatsList;

    public RAFileAndStats(long raFileDetailsId, String fileName, long fileReceivedTime, String status) {
        this.raFileDetailsId = raFileDetailsId;
        this.fileName = fileName;
        this.fileReceivedTime = fileReceivedTime;
        this.status = status;
        this.sheetStatsList = new ArrayList<>();
    }

    public void addSheetDetails(RASheetAndStats raSheetAndStats) {
        noOfSheets += 1;
        sheetStatsList.add(raSheetAndStats);
        increment(raSheetAndStats);
    }
}

package com.hilabs.rostertracker.dto;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RAFileAndStats extends RosterStats {
    private long raFileDetailsId;
    private String fileName;
    private Long noOfSheets;
    private Long fileReceivedTime;

    private String lob;
    private String market;
    private String plmTicketId;

    private String status;
    private List<RASheetAndStats> sheetStatsList;

    public RAFileAndStats(long raFileDetailsId, String fileName, long fileReceivedTime, String lob, String market, String plmTicketId, String status) {
        this.raFileDetailsId = raFileDetailsId;
        this.fileName = fileName;
        this.fileReceivedTime = fileReceivedTime;
        this.status = status;
        this.sheetStatsList = new ArrayList<>();
        this.market = market;
        this.lob = lob;
        this.plmTicketId = plmTicketId;
    }

    public void addSheetDetails(RASheetAndStats raSheetAndStats) {
        noOfSheets += 1;
        sheetStatsList.add(raSheetAndStats);
        increment(raSheetAndStats);
    }
}

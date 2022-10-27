package com.hilabs.rostertracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RosterSheetDetails {
    private Long raFileDetailsId;
    private List<SheetDetails> sheetDetailsList;
    private String lob;
    private String market;
    private String plmTicketId;
    private Long lastSavedTime;
    private String lastSavedBy;
    private Long lastApprovedTime;
    private String lastApprovedBy;
    private Long version;
}

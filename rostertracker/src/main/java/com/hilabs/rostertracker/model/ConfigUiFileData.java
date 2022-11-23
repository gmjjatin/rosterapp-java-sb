package com.hilabs.rostertracker.model;

import com.hilabs.roster.model.RosterSheetProcessStage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigUiFileData {
    private Long raFileDetailsId;
    private String originalFileName;
    private Long rosterReceivedTime;
    private String status;
    private Integer statusCode;
    private RosterSheetProcessStage stage;
    private String lob;
    private String market;
    private String plmTicketId;
    private boolean isEditable;
}

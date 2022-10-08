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
    private RosterSheetProcessStage stage;
    private boolean isAlreadyConfigured;
}

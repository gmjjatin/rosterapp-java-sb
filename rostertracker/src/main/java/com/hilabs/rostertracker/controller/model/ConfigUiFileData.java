package com.hilabs.rostertracker.controller.model;

import lombok.Data;

@Data
public class ConfigUiFileData {
    private Long raFileDetailsId;
    private String originalFileName;
    private Long rosterReceivedTime;
    private String status;
    private boolean isAlreadyConfigured;
}

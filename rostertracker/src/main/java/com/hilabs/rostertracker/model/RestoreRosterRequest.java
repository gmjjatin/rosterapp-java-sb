package com.hilabs.rostertracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RestoreRosterRequest implements Serializable {
    private String targetPhase;
    public RestoreRosterRequest() {}
}

package com.hilabs.rostertracker.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReleaseForDartUIRequest implements Serializable {
    private Long raFileDetailsId;
    private Long version;
}

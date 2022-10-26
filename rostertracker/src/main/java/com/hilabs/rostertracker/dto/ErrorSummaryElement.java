package com.hilabs.rostertracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorSummaryElement implements Serializable {
    private String category;
    private String type;
    private int count;
}

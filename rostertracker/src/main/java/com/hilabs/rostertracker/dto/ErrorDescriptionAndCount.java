package com.hilabs.rostertracker.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorDescriptionAndCount implements Serializable {
    private String description;
    private int count;
    public ErrorDescriptionAndCount(String description, int count) {
        this.description = description;
        this.count = count;
    }
}

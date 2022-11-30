package com.hilabs.rostertracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileUploadResponse implements Serializable {
    private String fileName;
    private long size;

    public FileUploadResponse() {}
}

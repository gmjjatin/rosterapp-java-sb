package com.hilabs.rapipeline.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DartStatusCheckResponse implements Serializable {
    private String reviewStatus;
    private String fileId;
    private String fileName;
    private Integer totalRecords;
    private String reviewStartDate;
    private String lastReviewDate;
    private Integer errorRecordCount;
    private Integer submittedRecordCount;

    public DartStatusCheckResponse() {}
}

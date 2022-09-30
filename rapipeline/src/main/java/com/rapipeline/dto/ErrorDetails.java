package com.rapipeline.dto;

import com.rapipeline.model.ErrorCategory;
import lombok.Data;

@Data
public class ErrorDetails {
    private ErrorCategory errorCategory;
    private String errorDescription;
    private String errorStackTrace;
    public ErrorDetails(ErrorCategory errorCategory, String errorDescription, String errorStackTrace) {
        this.errorCategory = errorCategory;
        this.errorDescription = errorDescription;
        this.errorStackTrace = errorStackTrace;
    }
}

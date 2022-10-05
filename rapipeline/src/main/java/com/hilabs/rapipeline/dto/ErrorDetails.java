package com.hilabs.rapipeline.dto;

import com.hilabs.rapipeline.model.ErrorCategory;
import lombok.Data;

@Data
public class ErrorDetails {
    String errorCode;
    String errorDescription;
    public ErrorDetails(String errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
}

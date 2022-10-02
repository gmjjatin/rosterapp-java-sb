package com.hilabs.rapipeline.dto;

import com.hilabs.rapipeline.model.ErrorCategory;
import lombok.Data;

@Data
public class ErrorDetails {
    String errorDescription;
    String errorLongDescription;
    public ErrorDetails(String errorDescription, String errorLongDescription) {
        this.errorDescription = errorDescription;
        this.errorLongDescription = errorLongDescription;
    }
}

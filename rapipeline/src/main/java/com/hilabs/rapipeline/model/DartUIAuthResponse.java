package com.hilabs.rapipeline.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class  DartUIAuthResponse implements Serializable {
    private String token;
    public DartUIAuthResponse() {}
}

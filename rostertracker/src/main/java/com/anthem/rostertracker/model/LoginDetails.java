package com.anthem.rostertracker.model;

import lombok.Data;

@Data
public class LoginDetails {
    private String token;
    private String userId;
    private String roleCD;
    private String firstName;
    private Boolean isDefaultPassword=false;
}

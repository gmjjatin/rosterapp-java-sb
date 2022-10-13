package com.hilabs.rostertracker.model;

import lombok.Data;

@Data
public class LoginDetails {
    private String token;
    private String username;
    private String roleCD;
    private String firstName;
    private Boolean isDefaultPassword=false;
}

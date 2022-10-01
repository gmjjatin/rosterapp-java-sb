package com.hilabs.rostertracker.model;

import lombok.Data;

@Data
public class UserDTO {
    private String userId;
    private String password;
    private String firstName;
    private String lastName;
    private String roleCd;
    private String createdORUpdatedUserId;
}
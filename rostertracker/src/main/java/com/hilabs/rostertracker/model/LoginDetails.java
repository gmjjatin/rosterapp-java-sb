package com.hilabs.rostertracker.model;

import com.hilabs.roster.entity.RAAuthPrivilege;
import com.hilabs.rostertracker.dto.RAAuthPrivilegeDTO;
import lombok.Data;

import java.util.List;

@Data
public class LoginDetails {
    private String token;
    private String username;
    private List<RAAuthPrivilegeDTO> privileges;
    private String firstName;
    private String showLogo;
    private String allowRosterUpload;
}

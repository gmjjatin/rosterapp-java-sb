package com.hilabs.rostertracker.dto;

import com.hilabs.roster.entity.RAAuthPrivilege;
import lombok.Data;



@Data
public class RAAuthPrivilegeDTO {

    private String privilegeName;
    private String privilegeDescription;
    private String privilegeType;
    private String resourceLocation;
    private String operationType;
    private Integer isActive;

    public static RAAuthPrivilegeDTO getInstance(RAAuthPrivilege raAuthPrivilege){
        if(raAuthPrivilege != null){
            RAAuthPrivilegeDTO dto = new RAAuthPrivilegeDTO();
            dto.setPrivilegeName(raAuthPrivilege.getPrivilegeName());
            dto.setPrivilegeDescription(raAuthPrivilege.getPrivilegeDescription());
            dto.setPrivilegeType(raAuthPrivilege.getPrivilegeType());
            dto.setResourceLocation(raAuthPrivilege.getResourceLocation());
            dto.setOperationType(raAuthPrivilege.getOperationType());
            dto.setIsActive(raAuthPrivilege.getIsActive());

            return dto;
        }
        return null;
    }

}

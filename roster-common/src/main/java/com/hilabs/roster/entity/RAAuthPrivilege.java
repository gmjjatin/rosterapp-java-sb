package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RA_AUTH_PRIVILEGE")
public class RAAuthPrivilege extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PRIVILEGE_NAME")
    private String privilegeName;

    @Column(name = "PRIVILEGE_DESCRIPTION")
    private String privilegeDescription;

    @Column(name = "PRIVILEGE_TYPE")
    private String privilegeType;

    @Column(name = "RESOURCE_LOCATION")
    private String resourceLocation;

    @Column(name = "OPERATION_TYPE")
    private String operationType;

    @Column(name = "is_active")
    private Integer isActive;


}

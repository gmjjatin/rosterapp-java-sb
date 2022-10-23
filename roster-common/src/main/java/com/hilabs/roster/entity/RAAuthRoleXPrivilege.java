package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RA_AUTH_ROLE_X_PRIVILEGE")
public class RAAuthRoleXPrivilege extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "PRIVILEGE_ID")
    private Long privilegeId;

    @Column(name = "is_active")
    private Integer isActive;


}

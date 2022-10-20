package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RA_AUTH_ROLE")
public class RAAuthRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @Column(name = "ROLE_DESCRIPTION")
    private String roleDescription;

    @Column(name = "is_active")
    private Integer isActive;


}

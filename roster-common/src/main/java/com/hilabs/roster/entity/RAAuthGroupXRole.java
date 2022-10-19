package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RA_AUTH_GROUP_X_ROLE")
public class RAAuthGroupXRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "is_active")
    private Integer isActive;


}

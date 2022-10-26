package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RA_AUTH_GROUP")
public class RAAuthGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "GROUP_DESCRIPTION")
    private String groupDescription;

    @Column(name = "is_active")
    private Integer isActive;


}

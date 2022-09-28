package com.anthem.rostertracker.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@Entity
@Table(name = "roster_user")
public class RosterUser {
    @Id
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "PWD")
    @JsonIgnore
    private String password;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "ACTIVE_FLAG")
    private Integer activeFlag;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_USER_ID")
    private String createdUserId;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_USER_ID")
    private String updatedUserId;

}


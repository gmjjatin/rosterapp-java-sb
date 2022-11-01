package com.hilabs.rostertracker.entity;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "roster_user")
@EntityListeners(AuditingEntityListener.class)
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
    @CreatedDate
    private Date createdDate;

    @Column(name = "CREATED_USER_ID")
    @CreatedBy
    private String createdUserId;

    @Column(name = "UPDATED_DATE")
    @LastModifiedDate
    private Date updatedDate;

    @Column(name = "UPDATED_USER_ID")
    @LastModifiedBy
    private String updatedUserId;
}


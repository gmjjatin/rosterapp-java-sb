package com.hilabs.roster.entity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@Entity
@Table(name = "roster_user_x_user_role")
public class RosterUserXRole {
    @Id
    @Column(name = "USER_ID")
    private String userId;
    @JsonIgnore
    @Column(name = "ROLE_CD")
    private String roleCD;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_USER_ID")
    private String createdUserId;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_USER_ID")
    private String updatedUserId;

}

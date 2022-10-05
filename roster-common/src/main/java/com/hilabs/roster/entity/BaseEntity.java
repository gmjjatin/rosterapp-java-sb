package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {
    @Column(name = "creat_dt")
    private Date createdDate;

    @Column(name = "last_updt_dt")
    private Date lastUpdatedDate;

    @Column(name = "creat_user_id")
    private String createdUserId;

    @Column(name = "last_updt_user_id")
    private String lastUpdatedUserId;

    public BaseEntity() {
        this.createdDate = new Date();
        this.lastUpdatedDate = new Date();
        this.createdUserId = "SYSTEM";
        this.lastUpdatedUserId = "SYSTEM";
    }
}

package com.hilabs.roster.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {
    @Column(name = "creat_dt")
    @CreatedDate
    private Date createdDate;

    @Column(name = "last_updt_dt")
    @LastModifiedDate
    private Date lastUpdatedDate;

    @Column(name = "creat_user_id")
    @CreatedBy
    private String createdUserId;

    @Column(name = "last_updt_user_id")
    @LastModifiedBy
    private String lastUpdatedUserId;

    public BaseEntity() {
        this.createdDate = new Date();
        this.lastUpdatedDate = new Date();
        this.createdUserId = "SYSTEM";
        this.lastUpdatedUserId = "SYSTEM";
    }
}

package com.hilabs.roster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RA_RT_USER_ACTN_AUDIT")
@Data
public class RAUserActionAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ACTN_OBJCT_ID")
    private String actionObjectId;

    @Column(name = "ACTN_OBJCT_TYPE")
    private String actionObjectType;

    @Column(name = "USER_ACTN")
    private String userAction;

    @Column(name = "creat_dt")
    @CreatedDate
    private Date createdDate;

    @Column(name = "creat_user_id")
    @CreatedBy
    private String createdUserId;

    public RAUserActionAudit(final String actionObjectId, final String actionObjectType, final String userAction, final Date createdDate,
                             final String createdUserId) {
        this.actionObjectId = actionObjectId;
        this.actionObjectType = actionObjectType;
        this.userAction = userAction;
        this.createdDate = createdDate;
        this.createdUserId = createdUserId;
    }

    @Override
    public String toString() {
        return "RAUserActionAudit [id=" + id + "]";
    }
}

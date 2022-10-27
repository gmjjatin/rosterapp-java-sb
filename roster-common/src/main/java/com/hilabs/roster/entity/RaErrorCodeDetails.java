package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_ERROR_CODE_DETAILS")
@Data
public class RaErrorCodeDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ERR_CD")
    private String errorCode;

    @Column(name = "ERR_CD_DESC")
    private String errorCodeDescription;

    @Column(name = "ERR_CD_CTGRY")
    private String errorCodeCategory;

    @Column(name = "ERR_CD_SCNDRY_CTGRY")
    private String errorCodeCategorySecondary;

    @Column(name = "ERR_STATUS_CD")
    private String errorCodeStatus;

    @Column(name = "ERR_STG")
    private String errorCodeStage;

    @Column(name = "ERR_CD_DSPLY_TMPLT_TXT")
    private String errorCodeDisplayTemplate;

    @Column(name = "ERR_CD_DSPLY_TMPLT_PARM")
    private String errorCodeTemplateParameters;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;
}

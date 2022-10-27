package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_SHEET_ERROR_CODE_DETAILS")
@Data
public class RASheetErrorCodeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RA_SHEET_DETAILS_ID")
    private Long raSheetDetailsId;

    @Column(name = "ERR_CD")
    private String errorCode;

    @Column(name = "ERR_CD_DSPLY_TMPLT_PARM")
    private String errorCodeTemplateParameters;

    @Column(name = "STATUS_CD")
    private Integer statusCode;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    public RASheetErrorCodeDetails() {}

    public RASheetErrorCodeDetails(final Long raSheetDetailsId, final String errorCode, final String errorCodeTemplateParameters, final Integer statusCode) {
        super();
        this.raSheetDetailsId = raSheetDetailsId;
        this.errorCode = errorCode;
        this.errorCodeTemplateParameters = errorCodeTemplateParameters;
        this.statusCode = statusCode;
    }
}

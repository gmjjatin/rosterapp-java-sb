package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "DART_RA_SHEET_ERROR_CODE_DETAILS")
@Data
public class RASheetErrorCodeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_sheet_details_id")
    private Long raSheetDetailsId;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "ERROR_CODE_TEMPLATE_PARAMETERS")
    private String errorCodeTemplateParameters;

    @Column(name = "status")
    private String status;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    public RASheetErrorCodeDetails() {}

    public RASheetErrorCodeDetails(final Long raSheetDetailsId, final String errorCode, final String errorCodeTemplateParameters, final String status) {
        super();
        this.raSheetDetailsId = raSheetDetailsId;
        this.errorCode = errorCode;
        this.errorCodeTemplateParameters = errorCodeTemplateParameters;
        this.status = status;
    }
}

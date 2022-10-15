package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "DART_RA_ERROR_CODE_DETAILS")
@Data
public class DartRaErrorCodeDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "ERROR_CODE_DESCRIPTION")
    private String errorCodeDescription;

    @Column(name = "ERROR_CODE_CATEGORY")
    private String errorCodeCategory;

    @Column(name = "ERROR_CODE_CATEGORY_SECONDARY")
    private String errorCodeCategorySecondary;

    @Column(name = "ERROR_CODE_STATUS")
    private String errorCodeStatus;

    @Column(name = "ERROR_CODE_STAGE")
    private String errorCodeStage;

    @Column(name = "ERROR_CODE_DISPLAY_TEMPLATE")
    private String errorCodeDisplayTemplate;

    @Column(name = "ERROR_CODE_TEMPLATE_PARAMETERS")
    private String errorCodeTemplateParameters;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;
}

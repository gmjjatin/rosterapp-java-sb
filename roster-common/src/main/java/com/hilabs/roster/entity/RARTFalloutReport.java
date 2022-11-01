package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RT_FALLOUT_REPORT")
@Data
public class RARTFalloutReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_sheet_details_id")
    private Integer raSheetDetailsId;

    @Column(name = "ra_row_id")
    private Integer raRowId;

    @Column(name = "rule_ctgry_stg")
    private Integer ruleCategoryStage;

    @Column(name = "ERR_TYPE")
    private String errorType;

    @Column(name = "ERR_DESC")
    private String errorDescription;

    @Column(name = "TRNSCTN_TYPE")
    private String transactionType;

    @Column(name = "RCMNDN_ACTN_TXT")
    private String recommendationAction;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    public RARTFalloutReport() {
    }
}

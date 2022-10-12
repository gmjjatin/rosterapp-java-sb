package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RC_FALLOUT_REPORT")
@Data
public class RARCFalloutReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_sheet_details_id")
    private Integer raSheetDetailsId;

    @Column(name = "ra_row_id")
    private Integer raRowId;

    @Column(name = "rule_ctgry_stage")
    private Integer ruleCategoryStage;

    @Column(name = "err_type")
    private String errorType;

    @Column(name = "err_dscrptn")
    private String errorDescription;

    @Column(name = "trnsctn_type")
    private String transactionType;

    @Column(name = "recommendation_action")
    private String recommendationAction;

    public RARCFalloutReport() {
    }
}

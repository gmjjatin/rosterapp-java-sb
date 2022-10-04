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
    private int raSheetDetailsId;

    @Column(name = "ra_row_id")
    private int raRowId;

    @Column(name = "rule_ctgry_stage")
    private int ruleCategoryStage;

    @Column(name = "err_type")
    private int errorType;

    @Column(name = "err_dscrptn")
    private int errorDescription;

    @Column(name = "trnsctn_type")
    private int transactionType;

    @Column(name = "recommendation_action")
    private int recommendationAction;

    public RARCFalloutReport() {
    }
}

package com.anthem.rostertracker.entity;

import com.anthem.rostertracker.model.RosterFileProcessStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ra_fallout_report")
@Data
public class RAFalloutReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "ra_sheet_details_id")
    private int raSheetDetailsId;

    @Column(name = "ra_row_id")
    private String raRowId;

    @Column(name = "rule_ctgry_stage")
    private String ruleCategoryStage;

    @Column(name = "err_type")
    private String errorType;

    @Column(name = "err_code")
    private String errorCode;

    @Column(name = "err_dscrptn")
    private String errorDescription;

    @Column(name = "trnsctn_type")
    private String transactionType;

    @Column(name = "recommended_action")
    private String recommendedAction;

    public RAFalloutReport() {
    }
}

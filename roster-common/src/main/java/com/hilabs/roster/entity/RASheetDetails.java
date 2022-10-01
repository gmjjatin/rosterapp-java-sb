package com.hilabs.roster.entity;

import com.hilabs.roster.model.RosterFileProcessStatus;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_sheet_details")
@Data
public class RASheetDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "ra_file_details_id")
    private long raFileDetailsId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RosterFileProcessStatus status;

    @Column(name = "name")
    private String name;

    //TODO enum??
    @Column(name = "type")
    private String type;

    @Column(name = "roster_record_cnt")
    private int rosterRecordCount;

    @Column(name = "auto_mapped_record_cnt")
    private int autoMappedRecordCount;

    @Column(name = "dart_record_cnt")
    private int dartRecordCount;

    @Column(name = "dart_row_cnt")
    private int dartRowCount;

    @Column(name = "successful_record_cnt")
    private int successfulRecordCount;

    @Column(name = "manual_review_record_cnt")
    private int manualReviewRecordCount;

    @Column(name = "sps_load_trnsctn_cnt")
    private int spsLoadTransactionCount;

    @Column(name = "sps_load_success_trnsctn_cnt")
    private int spsLoadSuccessTransactionCount;

    public RASheetDetails() {}

    @Override
    public String toString() {
        return "RASheetDetails [id=" + id + "]";
    }
}

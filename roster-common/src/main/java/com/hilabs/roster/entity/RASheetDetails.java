package com.hilabs.roster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "RA_RT_SHEET_DETAILS")
@Data
public class RASheetDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private Long raFileDetailsId;

    @Column(name = "status_cd")
    private Integer statusCode;

    @Column(name = "tab_nm")
    private String tabName;

    @Column(name = "type")
    private String type;

    @Column(name = "roster_record_cnt")
    private Integer rosterRecordCount;

    @Column(name = "auto_mapped_record_cnt")
    private Integer autoMappedRecordCount;

    @Column(name = "isf_record_cnt")
    private Integer isfRecordCount;

    @Column(name = "isf_row_cnt")
    private Integer isfRowCount;

    @Column(name = "out_row_cnt")
    private Integer outRowCount;

    @Column(name = "out_record_cnt")
    private Integer outRecordCount;

    @Column(name = "target_successful_record_cnt")
    private Integer targetSuccessfulRecordCount;

    @Column(name = "manual_review_record_cnt")
    private Integer manualReviewRecordCount;

    @Column(name = "target_load_trnsctn_cnt")
    private Integer targetLoadTransactionCount;

    @Column(name = "target_load_success_trnsctn_cnt")
    private Integer targetLoadSuccessTransactionCount;

    @Column(name = "isf_file_nm")
    private String isfFileName;

    @Column(name = "out_file_nm")
    private String outFileName;

    public RASheetDetails() {
    }

    @Override
    public String toString() {
        return "RASheetDetails [id=" + id + "]";
    }

    public RASheetDetails(final Long raFileDetailsId, final Integer statusCode, final String tabName, final String type, final Integer rosterRecordCount, final Integer autoMappedRecordCount, final Integer isfRecordCount, final Integer isfRowCount, final Integer outRowCount, final Integer outRecordCount, final Integer targetSuccessfulRecordCount, final Integer manualReviewRecordCount, final Integer targetLoadTransactionCount, final Integer targetLoadSuccessTransactionCount, final String isfFileName, final String outFileName) {
        super();
        this.raFileDetailsId = raFileDetailsId;
        this.statusCode = statusCode;
        this.tabName = tabName;
        this.type = type;
        this.rosterRecordCount = rosterRecordCount;
        this.autoMappedRecordCount = autoMappedRecordCount;
        this.isfRecordCount = isfRecordCount;
        this.isfRowCount = isfRowCount;
        this.outRowCount = outRowCount;
        this.outRecordCount = outRecordCount;
        this.targetSuccessfulRecordCount = targetSuccessfulRecordCount;
        this.manualReviewRecordCount = manualReviewRecordCount;
        this.targetLoadTransactionCount = targetLoadTransactionCount;
        this.targetLoadSuccessTransactionCount = targetLoadSuccessTransactionCount;
        this.isfFileName = isfFileName;
        this.outFileName = outFileName;
    }
}

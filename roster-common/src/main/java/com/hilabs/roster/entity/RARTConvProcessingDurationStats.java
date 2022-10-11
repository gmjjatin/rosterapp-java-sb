package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RA_RT_CONV_PROCESSING_DURATION_STATS")
@Data
public class RARTConvProcessingDurationStats extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO Has an type in details word
    @Column(name = "ra_sheet_details_id")
    private Long raSheetDetailsId;

    @Column(name = "status_cd")
    private Integer statusCode;

    @Column(name = "start_dt")
    private Date startDate;

    @Column(name = "cmpltn_dt")
    private Date completionDate;

    public RARTConvProcessingDurationStats() {}

    @Override
    public String toString() {
        return "RASheetDetails [id=" + id + "]";
    }
}

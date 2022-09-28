package com.anthem.rostertracker.entity;

import com.anthem.rostertracker.model.RosterFileProcessStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ra_conv_processing_duration_stats")
@Data
public class RAConvProcessingDurationStats extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "ra_sheet_details_id")
    private int raSheetDetailsId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private RosterFileProcessStatus status;

    @Column(name = "start_dt")
    private Date startDate;

    @Column(name = "cmpltn_dt")
    private Date completionDate;

    public RAConvProcessingDurationStats() {
    }
}

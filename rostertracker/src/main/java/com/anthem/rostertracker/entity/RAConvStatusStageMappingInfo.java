package com.anthem.rostertracker.entity;

import com.anthem.rostertracker.model.RosterFileProcessStage;
import com.anthem.rostertracker.model.RosterFileProcessStatus;
import com.anthem.rostertracker.model.RosterFileProcessStatusPosition;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_conv_status_stage_mappings")
@Data
public class RAConvStatusStageMappingInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "prcssng_status")
    private RosterFileProcessStatus processingStatus;

    @Column(name = "stage")
    private RosterFileProcessStage stage;

    @Column(name = "status")
    private RosterFileProcessStatusPosition statusPosition;

    public RAConvStatusStageMappingInfo() {}

    @Override
    public String toString() {
        return "RAConvStatusStageMappingInfo [id=" + id + "]";
    }
}

package com.hilabs.roster.entity;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterFileProcessStatus;
import com.hilabs.roster.model.RosterFileProcessStatusPosition;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_conv_status_stage_mappings")
@Data
public class RAConvStatusStageMappingInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "prcssng_status")
    private RosterFileProcessStatus processingStatus;

    @Column(name = "stage")
    private RosterSheetProcessStage stage;

    @Column(name = "status")
    private RosterFileProcessStatusPosition statusPosition;

    public RAConvStatusStageMappingInfo() {}

    @Override
    public String toString() {
        return "RAConvStatusStageMappingInfo [id=" + id + "]";
    }
}

package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RC_ROSTER_ISF_MAP")
@Data
public class RARCRosterISFMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RA_SHEET_DETAILS_ID")
    private Long raSheetDetailsId;

    @Column(name = "ROSTER_COLUMN_NM")
    private String rosterColumnName;

    @Column(name = "ISF_COLUMN_NM")
    private String isfColumnName;

    @Column(name = "COLUMN_MAPPING_RNK")
    private Integer columnMappingRank;
    
    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    public RARCRosterISFMap() {}
}

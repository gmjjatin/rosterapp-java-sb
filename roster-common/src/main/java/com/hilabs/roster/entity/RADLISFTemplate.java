package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_DL_ISF_TEMPLATE")
@Data
public class RADLISFTemplate extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ISF_COLUMN_NM")
    private String isfColumnName;

    @Column(name = "ALT_DESCRIPTION_SYNONYMS")
    private String ALT_DESCRIPTION_SYNONYMS;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;
}

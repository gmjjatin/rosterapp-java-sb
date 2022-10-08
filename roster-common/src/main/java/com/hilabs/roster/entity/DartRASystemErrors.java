package com.hilabs.roster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "DART_RA_SYSTEM_ERRORS")
@Data
@AllArgsConstructor
public class DartRASystemErrors extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RA_FILE_DETAILS_ID")
    private Long raFileDetailsId;

    @Column(name = "RA_SHEET_DETAILS_ID")
    private Long raSheetDetailsId;

    @Column(name = "LAST_STAGE")
    private String lastStage;

    @Column(name = "LAST_STATUS")
    private String lastStatus;

    @Column(name = "ERROR_CATEGORY")
    private String ERROR_CATEGORY;

    @Column(name = "ERROR_DESCRIPTION")
    private String ERROR_DESCRIPTION;

    @Column(name = "ERROR_STACK_TRACE")
    private String ERROR_STACK_TRACE;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    public DartRASystemErrors() {}

    public DartRASystemErrors(final Long raFileDetailsId, final Long raSheetDetailsId, final String lastStage, final String lastStatus, final String ERROR_CATEGORY, final String ERROR_DESCRIPTION, final String ERROR_STACK_TRACE, final Integer isActive) {
        super();
        this.raFileDetailsId = raFileDetailsId;
        this.raSheetDetailsId = raSheetDetailsId;
        this.lastStage = lastStage;
        this.lastStatus = lastStatus;
        this.ERROR_CATEGORY = ERROR_CATEGORY;
        this.ERROR_DESCRIPTION = ERROR_DESCRIPTION;
        this.ERROR_STACK_TRACE = ERROR_STACK_TRACE;
        this.isActive = isActive;
    }



}

package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_ERROR_LOGS")
@Data
public class RAErrorLogs extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private Long raFileDetailsId;

    @Column(name = "ra_sheet_details_id")
    private Long raSheetDetailsId;

    @Column(name = "stage_nm")
    private String stageName;

    @Column(name = "error_cd_details_id")
    private Long errorCodeDetailsId;

    @Column(name = "error_desc")
    private String errorDescription;

    @Column(name = "error_long_desc")
    private String errorLongDescription;

    public RAErrorLogs() {}

    public RAErrorLogs(Long raFileDetailsId, Long raSheetDetailsId, String stageName, Long errorCodeDetailsId, String errorDescription,
                       String errorLongDescription) {
        this.raFileDetailsId = raFileDetailsId;
        this.raSheetDetailsId = raSheetDetailsId;
        this.stageName = stageName;
        this.errorCodeDetailsId = errorCodeDetailsId;
        this.errorDescription = errorDescription;
        this.errorLongDescription = errorLongDescription;
    }
}
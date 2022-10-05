package com.hilabs.roster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "DART_RA_FILE_ERROR_CODE_DETAILS")
@Data
public class RAFileErrorCodeDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private Long raFileDetailsId;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "ERROR_CODE_TEMPLATE_PARAMETERS")
    private String errorCodeTemplateParameters;

    @Column(name = "status")
    private String status;

    public RAFileErrorCodeDetails() {}

    public RAFileErrorCodeDetails(final Long raFileDetailsId, final String errorCode, final String errorCodeTemplateParameters, final String status) {
        super();
        this.raFileDetailsId = raFileDetailsId;
        this.errorCode = errorCode;
        this.errorCodeTemplateParameters = errorCodeTemplateParameters;
        this.status = status;
    }
}
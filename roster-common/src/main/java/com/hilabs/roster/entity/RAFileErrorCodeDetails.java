package com.hilabs.roster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_FILE_ERROR_CODE_DETAILS")
@Data
public class RAFileErrorCodeDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private Long raFileDetailsId;

    @Column(name = "ERR_CD")
    private String errorCode;

    @Column(name = "ERR_CD_DSPLY_TMPLT_PARM")
    private String errorCodeTemplateParameters;

    @Column(name = "STATUS_CD")
    private Integer statusCode;

    public RAFileErrorCodeDetails() {}

    public RAFileErrorCodeDetails(final Long raFileDetailsId, final String errorCode, final String errorCodeTemplateParameters, final Integer status) {
        super();
        this.raFileDetailsId = raFileDetailsId;
        this.errorCode = errorCode;
        this.errorCodeTemplateParameters = errorCodeTemplateParameters;
        this.statusCode = status;
    }
}
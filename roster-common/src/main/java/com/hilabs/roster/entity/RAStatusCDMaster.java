package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RT_STATUS_CD_MSTR")
@Data
public class RAStatusCDMaster extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status_cd")
    private Integer statusCode;

    @Column(name = "bsns_status")
    private String businessStatus;

    @Column(name = "status_desc")
    private String statusDescription;

    @Column(name = "stage_nm")
    private String stageName;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "is_cmplt_status")
    private Integer isCompleteStatus;

    @Column(name = "is_fail_status")
    private Integer isFailStatus;

    public RAStatusCDMaster() {}
    @Override
    public String toString() {
        return "RAStatusCDMaster [id=" + id + "]";
    }
}

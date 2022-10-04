package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RT_FILE_DETAILS")
@Data
public class RAFileDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_provider_details_id")
    private Long raProvDetailsId;

    @Column(name = "orgnl_file_nm")
    private String originalFileName;

    @Column(name = "stndrdzd_file_nm")
    private String standardizedFileName;

    @Column(name = "market")
    private String market;

    @Column(name = "status_cd")
    private Integer statusCode;

    @Column(name = "is_active")
    private Integer isActive;

    public RAFileDetails() {}
    @Override
    public String toString() {
        return "RAFileDetails [id=" + id + "]";
    }
}

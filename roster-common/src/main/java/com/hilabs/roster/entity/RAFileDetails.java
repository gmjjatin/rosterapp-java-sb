package com.hilabs.roster.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RA_RT_FILE_DETAILS")
@Data
public class RAFileDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "ra_provider_details_id")
//    private Long raProvDetailsId;

    @Column(name = "orgnl_file_nm")
    private String originalFileName;

    @Column(name = "stndrdzd_file_nm")
    private String standardizedFileName;

    @Column(name = "market")
    private String market;

    @Column(name = "status_cd")
    private Integer statusCode;

    @Column(name = "MANUAL_ACTN_REQ")
    private Integer manualActionRequired;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "source_nm")
    private String sourceName;

    @Column(name = "last_saved_dt")
    private Date lastSavedDate;

    @Column(name = "last_approved_dt")
    private Date lastApprovedDate;

    @Column(name = "last_saved_by")
    private String lastSavedBy;

    @Column(name = "last_approved_by")
    private String lastApprovedBy;

    @Version
    private Long version;

    public RAFileDetails() {}
    @Override
    public String toString() {
        return "RAFileDetails [id=" + id + "]";
    }
}

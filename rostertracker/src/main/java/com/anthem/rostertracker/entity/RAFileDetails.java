package com.anthem.rostertracker.entity;

import com.anthem.rostertracker.model.RosterFileProcessStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ra_file_details")
@Data
public class RAFileDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "ra_prov_details_id")
    private Long raProvDetailsId;

    @Column(name = "orgnl_file_nm")
    private String originalFileName;

    @Column(name = "stndrdzd_file_nm")
    private String standardizedFileName;

    @Column(name = "plm_ticket_id")
    private String plmTicketId;

    @Column(name = "file_location")
    private String fileLocation;

    @Column(name = "file_system")
    private String fileSystem;

    @Column(name = "is_active")
    private Integer isActive;

    public RAFileDetails() {}

    @Override
    public String toString() {
        return "RAFileDetails [id=" + id + "]";
    }
}

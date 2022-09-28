package com.rapipeline.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_file_meta_data_details")
@Data
public class RAFileMetaDataDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "market")
    private String market;

    @Column(name = "line_of_business")
    private String lineOfBusiness;

    @Column(name = "plm_ticket_id")
    private String plmTicketId;

    @Column(name = "password")
    private String password;

    @Column(name = "is_active")
    private int isActive;

    @Column(name = "ingestion_status")
    private int ingestionStatus;

    @Column(name = "retry_no")
    private int retryNo;

    public RAFileMetaDataDetails() {}

    @Override
    public String toString() {
        return "RAFileMetaDataDetails [id=" + id + "]";
    }
}

package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_file_x_status")
@Data
public class RAFileXStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Referential Integrity
    @Column(name = "ra_file_details_id")
    private long raFileDetailsId;

    @Column(name = "status_code")
    private int statusCode;

    public RAFileXStatus() {}

    @Override
    public String toString() {
        return "RAPlmRoFileData [id=" + id + "]";
    }
}
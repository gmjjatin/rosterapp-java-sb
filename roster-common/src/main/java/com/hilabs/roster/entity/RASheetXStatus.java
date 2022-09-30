package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ra_sheet_x_status")
@Data
public class RASheetXStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Referential Integrity
    @Column(name = "ra_sheet_details_id")
    private long raSheetDetailsId;

    @Column(name = "status_code")
    private int statusCode;

    public RASheetXStatus() {}

    @Override
    public String toString() {
        return "RAPlmRoFileData [id=" + id + "]";
    }
}

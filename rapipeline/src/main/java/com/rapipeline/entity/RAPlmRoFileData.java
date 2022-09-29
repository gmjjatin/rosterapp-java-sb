package com.rapipeline.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ra_plm_ro_file_data")
@Data
public class RAPlmRoFileData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ra_plm_ro_file_data_id")
    private Long raPlmRoFileDataId;

    //Referential Integrity
    @Column(name = "ra_plm_ro_prof_data_id")
    private Long raPlmRoProfDataId;

    //Roster Filename
    @Column(name = "file_nm")
    private String fileName;

    //Document Id
    @Column(name = "dcn_id")
    private String dcnId;

    //Roster File Size
    @Column(name = "file_size")
    private String fileSize;

    //RA File Processing status [ NEW, IN-PROGRESS,COMPLETE, REJECTED,PENDING ]
    @Column(name = "ra_file_prcs_stts")
    private String raFileProcessingStatus;

    //Deposit Date
    @Column(name = "deposit_dt")
    private Date depositDate;

    //File Document number
    @Column(name = "f_docnum")
    private String fileDocumentNumber;

    public RAPlmRoFileData() {}

    @Override
    public String toString() {
        return "RAPlmRoFileData [id=" + raPlmRoFileDataId + "]";
    }
}

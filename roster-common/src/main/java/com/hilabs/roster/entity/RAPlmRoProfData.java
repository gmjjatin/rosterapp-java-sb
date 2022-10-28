package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ra_plm_ro_prof_data")
@Data
public class RAPlmRoProfData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ra_plm_ro_prof_data_id")
    private Long raPlmRoProfDataId;

    //Top level case that tracks all work related to a Roster request that came in from any channel
    @Column(name = "ro_id")
    private String roId;

    //SPS T-case for tracking all work related to SPS updates whether through DART, PLM or Manual.
    @Column(name = "t_case_id")
    private String tCaseId;

    //Child case for tracking the RA-DART work. RA-DART system will update the status of this case along its lifecycle.
    @Column(name = "racd_id")
    private String racdId;

    //Child case for tracking and in future automating the TERM transactions. RA-DART system will create this case only if the roster has any TERM transactions in it.
    @Column(name = "ract_id")
    private String ractId;

    //Child case for tracking the Fallouts. RA_DART system will create this case along with attachments if fallout is encountered anytime during the RA-DART processing.
    @Column(name = "racf_id")
    private String racfId;

    //Master Provider ID  NUMBER
    @Column(name = "npi")
    private Long npi;

    //Tax Id
    @Column(name = "tax_id")
    private String taxId;

    //organization Name
    @Column(name = "org_nm")
    private String orgName;

    //Corporate State
    @Column(name = "cnt_state")
    private String cntState;

    //Commercial / Medicaid/Medicaid
    @Column(name = "lob")
    private String lob;

    //Corporate Receipt Date
    @Column(name = "corp_recipt_dt")
    private Date corporateReceiptDate;

    @Column(name = "IS_FILE_DLD_COMP")
    private String isFileDLDCOMP;

    public RAPlmRoProfData() {}

    @Override
    public String toString() {
        return "RAPlmRoProfData [id=" + raPlmRoProfDataId + "]";
    }
}

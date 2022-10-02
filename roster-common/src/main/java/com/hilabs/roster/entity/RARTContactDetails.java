package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RT_CONTACT_DETAILS")
@Data
public class RARTContactDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private Long raFileDetailsId;

    @Column(name = "ra_sheet_details_id")
    private Long raSheetDetailsId;

    @Column(name = "contact")
    private String contact;

    @Column(name = "contact_type")
    private String contactType;

    @Column(name = "is_active")
    private Integer isActive;

    public RARTContactDetails() {}

    public RARTContactDetails(Long raFileDetailsId, Long raSheetDetailsId, String contact, String contactType, Integer isActive) {
        this.raFileDetailsId = raFileDetailsId;
        this.raSheetDetailsId = raSheetDetailsId;
        this.contact = contact;
        this.contactType = contactType;
        this.isActive = isActive;
    }
    @Override
    public String toString() {
        return "RARTFileAltIds [id=" + id + "]";
    }
}

package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RT_FILE_ALT_IDS")
@Data
public class RARTFileAltIds extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private Long raFileDetailsId;

    @Column(name = "alt_id")
    private String altId;

    @Column(name = "alt_id_type")
    private String altIdType;

    @Column(name = "is_active")
    private Integer isActive;

    public RARTFileAltIds() {}

    public RARTFileAltIds(Long raFileDetailsId, String altId, String altIdType, Integer isActive) {
        this.raFileDetailsId = raFileDetailsId;
        this.altId = altId;
        this.altIdType = altIdType;
        this.isActive = isActive;
    }
    @Override
    public String toString() {
        return "RARTFileAltIds [id=" + id + "]";
    }
}

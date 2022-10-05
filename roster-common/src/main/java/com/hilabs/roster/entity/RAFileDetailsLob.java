package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "RA_RT_FILE_DETAILS_LOB")
@Data
public class RAFileDetailsLob extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ra_file_details_id")
    private Long raFileDetailsId;

    @Column(name = "lob")
    private String lob;

    @Column(name = "is_active")
    private Integer isActive;

    public RAFileDetailsLob() {}

    public RAFileDetailsLob(Long raFileDetailsId, String lob, Integer isActive) {
        super();
        this.raFileDetailsId = raFileDetailsId;
        this.lob = lob;
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "RAFileDetailsLob [id=" + id + "]";
    }
}

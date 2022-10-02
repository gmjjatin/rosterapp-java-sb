package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "RA_RT_PROVIDER_DETAILS")
@Data
public class RAProvDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_nm")
    private String sourceName;

    @Column(name = "is_active")
    private Integer isActive;

    public RAProvDetails() {}

    @Override
    public String toString() {
        return "RAProvDetails [id=" + id + "]";
    }
}

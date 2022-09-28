package com.anthem.rostertracker.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "ra_prov_details")
@Data
public class RAProvDetails extends BaseEntity {
    @Id
    //TODO manikanta change primitive to object(whereever needed)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "src_nm")
    private String sourceName;

    @Column(name = "market")
    private String market;

    @Column(name = "is_active")
    private Integer isActive;

    //TODO handle multiple line of business
    @Column(name = "lob")
    private String lineOfBusiness;

    public RAProvDetails() {}

    @Override
    public String toString() {
        return "RAProvDetails [id=" + id + "]";
    }
}

package com.hilabs.roster.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "RA_RT_MARKET_LOB_VALD")
@Data
public class RARTMarketLobVald extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market")
    private String market;

    @Column(name = "lob")
    private String lob;

    @Column(name = "is_active")
    private Integer isActive;
}

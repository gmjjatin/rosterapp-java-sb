//package com.hilabs.roster.entity;
//
//import lombok.Data;
//
//import javax.persistence.*;
//
//
//@Entity
//@Table(name = "RA_RT_PROV_MARKET_LOB_MAP")
//@Data
//public class RAProvMarketLobMap extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "ra_prov_details_id")
//    private Long raProvDetailsId;
//
//    @Column(name = "market")
//    private String market;
//
//    @Column(name = "lob")
//    private String lob;
//
//    @Column(name = "is_active")
//    private Integer isActive;
//
//    public RAProvMarketLobMap() {}
//
//    public RAProvMarketLobMap(Long raProvDetailsId, String market, String lob, Integer isActive) {
//        this.raProvDetailsId = raProvDetailsId;
//        this.market = market;
//        this.lob = lob;
//        this.isActive = isActive;
//    }
//
//    @Override
//    public String toString() {
//        return "RAProvDetails [id=" + id + "]";
//    }
//}

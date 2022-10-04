package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAProvMarketLobMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RAProvMarketLobMapRepository extends JpaRepository<RAProvMarketLobMap, Long> {
    @Query(value = "select * from RA_RT_PROV_MARKET_LOB_MAP where ra_prov_details_id = :raProvDetailsId",
            nativeQuery = true)
    List<RAProvMarketLobMap> getRAProvMarketLobMapForProvider(long raProvDetailsId);

    @Query(value = "select distinct(market) from RA_RT_PROV_MARKET_LOB_MAP", nativeQuery = true)
    List<String> findAllMarkets();

    @Query(value = "select distinct(lob) from RA_RT_PROV_MARKET_LOB_MAP", nativeQuery = true)
    List<String> findAllLineOfBusinesses();
}

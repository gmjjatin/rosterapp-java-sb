package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RARTMarketLobVald;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RARTMarketLobValdRepository extends JpaRepository<RARTMarketLobVald, Long> {
    @Query(value = "select * from RA_RT_MARKET_LOB_VALD where market = :market",
            nativeQuery = true)
    List<RARTMarketLobVald> getByMarket(String market);
}

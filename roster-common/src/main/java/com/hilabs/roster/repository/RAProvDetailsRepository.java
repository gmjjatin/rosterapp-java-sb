package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAProvDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RAProvDetailsRepository extends JpaRepository<RAProvDetails, Long> {
    @Query(value = "select * from ra_prov_details where UPPER(src_nm) like '%' || UPPER(:providerSearchStr) || '%'", nativeQuery = true)
    List<RAProvDetails> findByProviderSearchStr(@Param("providerSearchStr") String providerSearchStr);

    @Query(value = "select * from ra_prov_details where UPPER(market) like '%' || UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAProvDetails> findByMarketSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select * from ra_prov_details where UPPER(lob) like '%' || UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAProvDetails> findByLineOfBusinessSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select * from ra_prov_details where src_nm= :provider", nativeQuery = true)
    Optional<RAProvDetails> findByProvider(String provider);

    @Query(value = "select * from ra_prov_details where id = :providerId", nativeQuery = true)
    RAProvDetails findByProviderById(Integer providerId);

    @Query(value = "select * from ra_prov_details where market= :market and UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%'", nativeQuery = true)
    List<RAProvDetails> findByMarketAndLineOfBusiness(String market, String lineOfBusiness);

    @Query(value = "select * from ra_prov_details where market= :market", nativeQuery = true)
    List<RAProvDetails> findByMarket(String market);

    @Query(value = "select * from ra_prov_details where UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%'", nativeQuery = true)
    List<RAProvDetails> findByLineOfBusiness(String lineOfBusiness);

    @Query(value = "select * from ra_prov_details order by src_nm fetch next :noOfEntries rows only", nativeQuery = true)
    List<RAProvDetails> findTopEntriesOrderBySourceName(@Param("noOfEntries") int noOfEntries);

    @Query(value = "select * from ra_prov_details where id in (:raProvIds)", nativeQuery = true)
    List<RAProvDetails> findRAProvDetailsFromIds(List<Long> raProvIds);

    @Query(value = "select distinct(market) from ra_prov_details", nativeQuery = true)
    List<String> findAllMarkets();

    @Query(value = "select distinct(lob) from ra_prov_details", nativeQuery = true)
    List<String> findAllLineOfBusinesses();

    @Query(value = "select * from ra_prov_details", nativeQuery = true)
    List<RAProvDetails> getAllProviders();
}
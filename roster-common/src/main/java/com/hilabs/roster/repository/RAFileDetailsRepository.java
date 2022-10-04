package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAProvDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RAFileDetailsRepository extends CrudRepository<RAFileDetails, Long> {

    @Modifying
    @Transactional
    @Query(value = "update RA_RT_FILE_DETAILS set ra_provider_details_id = :raProvDetailsId, market = :market, lob = :lob," +
            " orgnl_file_nm = :originalFileName, stndrdzd_file_nm = :standardizedFileName, plm_ticket_id = :plmTicketId," +
            " file_location = :fileLocation, file_system = :fileSystem, last_updt_user_id = :lastUpdateUserId," +
            " last_updt_dt = sysdate " +
            "where id = :raProvDetailsId", nativeQuery = true)
    void updateRAFileDetails(@Param("raProvDetailsId") Long raProvDetailsId,
                             @Param("market") String market,
                             @Param("lob") String lob,
                             @Param("originalFileName") String originalFileName,
                             @Param("standardizedFileName") String standardizedFileName,
                             @Param("plmTicketId") String plmTicketId,
                             @Param("fileLocation") String fileLocation,
                             @Param("fileSystem") String fileSystem,
                             @Param("lastUpdateUserId") Long lastUpdateUserId);

    //TODO is fileName unique
    @Query(value = "select * from RA_RT_FILE_DETAILS where orgnl_file_nm = :fileName order by creat_dt desc fetch next 1 rows only",
            nativeQuery = true)
    Optional<RAFileDetails> findByFileName(@Param("fileName") String fileName);

    @Query(value = "select * from RA_RT_FILE_DETAILS where id = :raFileDetailsId", nativeQuery = true)
    Optional<RAFileDetails> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);

    //TODO
    @Query(value = "select * from RA_RT_FILE_DETAILS where ra_provider_details_id in (:raProvDetailsIds) " +
            "and creat_dt >= :startDate and creat_dt < :endDate order by creat_dt desc offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findRAFileDetailsListBetweenDatesFromRAProvDetailsIds(Date startDate, Date endDate, List<Long> raProvDetailsIds, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "select * from RA_RT_FILE_DETAILS where UPPER(orgnl_file_nm) like '%' || UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAFileDetails> findByFileSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS_LOB, RA_RT_FILE_DETAILS" +
            " where RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id and UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%'", nativeQuery = true)
    List<RAFileDetails> findByLineOfBusiness(String lineOfBusiness);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS, RA_RT_FILE_DETAILS_LOB " +
            "where RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id and market= :market " +
            "and UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%'", nativeQuery = true)
    List<RAFileDetails> findByMarketAndLineOfBusiness(String market, String lineOfBusiness);

    @Query(value = "select * from RA_RT_FILE_DETAILS where market= :market", nativeQuery = true)
    List<RAFileDetails> findByMarket(String market);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS, RA_RT_FILE_DETAILS_LOB where " +
            " RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id " +
            "and UPPER(lob) like '%' || UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAFileDetails> findByLineOfBusinessSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select * from RA_RT_FILE_DETAILS where UPPER(market) like '%' || UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAFileDetails> findByMarketSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select * from RA_RT_FILE_DETAILS where creat_dt >= :startDate and creat_dt < :endDate order by creat_dt desc offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findRAFileDetailsListBetweenDates(Date startDate, Date endDate, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "select distinct(market) from RA_RT_FILE_DETAILS", nativeQuery = true)
    List<String> findAllMarkets();
}
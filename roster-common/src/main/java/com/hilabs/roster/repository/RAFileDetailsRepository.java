package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAFileDetails;
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

    @Query(value = "select * from RA_RT_FILE_DETAILS where UPPER(orgnl_file_nm) like UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAFileDetails> findByFileSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS, RA_RT_FILE_DETAILS_LOB where " +
            " RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id " +
            "and UPPER(lob) like '%' || UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAFileDetails> findByLineOfBusinessSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select * from RA_RT_FILE_DETAILS where UPPER(market) like '%' || UPPER(:searchStr) || '%'", nativeQuery = true)
    List<RAFileDetails> findByMarketSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select distinct(market) from RA_RT_FILE_DETAILS where status_cd in (:statusCodes)", nativeQuery = true)
    List<String> findAllMarkets(List<Integer> statusCodes);

    @Query(value = "select * from RA_RT_FILE_DETAILS where creat_dt >= :startDate and creat_dt < :endDate and status_cd in (:statusCodes) " +
            "and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0" +
            " order by creat_dt desc offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findRAFileDetailsListBetweenDates(Date startDate, Date endDate, List<Integer> statusCodes, int limit, int offset, List<String> types);

    @Query(value = "select count(*) from RA_RT_FILE_DETAILS where creat_dt >= :startDate and creat_dt < :endDate and status_cd in (:statusCodes) " +
            "and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0 ",
            nativeQuery = true)
    Integer countRAFileDetailsListBetweenDates(Date startDate, Date endDate, List<Integer> statusCodes, List<String> types);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS_LOB, RA_RT_FILE_DETAILS" +
            " where RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id and " +
            "RA_RT_FILE_DETAILS.creat_dt >= :startDate and RA_RT_FILE_DETAILS.creat_dt < :endDate " +
            "and UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%' and status_cd in (:statusCodes) " +
            "and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0 " +
            "order by RA_RT_FILE_DETAILS.creat_dt desc offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findByLineOfBusiness(String lineOfBusiness, Date startDate, Date endDate, List<Integer> statusCodes,
                                             int limit, int offset, List<String> types);

    @Query(value = "select count(RA_RT_FILE_DETAILS.*) from RA_RT_FILE_DETAILS_LOB, RA_RT_FILE_DETAILS" +
            " where RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id and " +
            "RA_RT_FILE_DETAILS.creat_dt >= :startDate and RA_RT_FILE_DETAILS.creat_dt < :endDate " +
            "and UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%' and status_cd in (:statusCodes) " +
            "and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0 ",
            nativeQuery = true)
    Integer countByLineOfBusiness(String lineOfBusiness, Date startDate, Date endDate, List<Integer> statusCodes, List<String> types);

    //TODO demo handle limit and offset
    @Query(value = "select * from RA_RT_FILE_DETAILS where market= :market and creat_dt >= :startDate " +
            "and creat_dt < :endDate and status_cd in (:statusCodes) " +
            "(select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0 " +
            "order by creat_dt desc offset :offset " +
            "rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findByMarket(String market, Date startDate, Date endDate, List<Integer> statusCodes, int limit, int offset,
                                     List<String> types);

    @Query(value = "select count(*) from RA_RT_FILE_DETAILS where market= :market and creat_dt >= :startDate " +
            "and creat_dt < :endDate and status_cd in (:statusCodes) " +
            "(select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0 " +
            "order by creat_dt desc offset :offset rows fetch next :limit rows only", nativeQuery = true)
    Integer countByMarket(String market, Date startDate, Date endDate, List<Integer> statusCodes, List<String> types);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS, RA_RT_FILE_DETAILS_LOB " +
            "where RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id and market= :market " +
            "and UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%' and status_cd in (:statusCodes) " +
            "and RA_RT_FILE_DETAILS.creat_dt >= :startDate and RA_RT_FILE_DETAILS.creat_dt < :endDate " +
            "and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0 " +
            "order by RA_RT_FILE_DETAILS.creat_dt desc" +
            " offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findByMarketAndLineOfBusiness(String market, String lineOfBusiness, Date startDate, Date endDate,
                                                      List<Integer> statusCodes, List<String> types, int limit, int offset);

    @Query(value = "select count(RA_RT_FILE_DETAILS.*) from RA_RT_FILE_DETAILS, RA_RT_FILE_DETAILS_LOB " +
            "where RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id and market= :market " +
            "and UPPER(lob) like '%' || UPPER(:lineOfBusiness) || '%' and status_cd in (:statusCodes) " +
            "and RA_RT_FILE_DETAILS.creat_dt >= :startDate and RA_RT_FILE_DETAILS.creat_dt < :endDate " +
            "and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) > 0",
            nativeQuery = true)
    Integer countByMarketAndLineOfBusiness(String market, String lineOfBusiness, Date startDate, Date endDate, List<Integer> statusCodes,
                                           List<String> types);
    @Query(value = "select * from RA_RT_FILE_DETAILS where status_cd in (:statusCodes) and MANUAL_ACTN_REQ in (:manualActionRequiredList) " +
            "offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findFileDetailsByStatusCodesWithManualActionReqList(List<Integer> statusCodes, List<Integer> manualActionRequiredList, int limit, int offset);

    @Query(value = "select * from RA_RT_FILE_DETAILS where status_cd in (:statusCodes) " +
            "offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findFileDetailsByStatusCodes(List<Integer> statusCodes, int limit, int offset);

    @Modifying
    @Transactional
    @Query(value = "update RA_RT_FILE_DETAILS set status_cd = :statusCode where id = :raFileDetailsId", nativeQuery = true)
    void updateRAFileDetailsStatus(Long raFileDetailsId, Integer statusCode);

    @Modifying
    @Transactional
    @Query(value = "update RA_RT_FILE_DETAILS set MANUAL_ACTN_REQ = :manualActionRequired where id = :raFileDetailsId", nativeQuery = true)
    void updateManualActionRequiredInRAFileDetails(Long raFileDetailsId, Integer manualActionRequired);
}
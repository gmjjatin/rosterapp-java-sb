package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAFileDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RAFileDetailsRepository extends CrudRepository<RAFileDetails, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<RAFileDetails> findRAFileDetailsById(Long id);

    //TODO is fileName unique
    @Query(value = "select * from RA_RT_FILE_DETAILS where orgnl_file_nm = :fileName and is_active = 1 order by creat_dt desc fetch next 1 rows only",
            nativeQuery = true)
    Optional<RAFileDetails> findByFileName(@Param("fileName") String fileName);

    @Query(value = "select * from RA_RT_FILE_DETAILS where id = :raFileDetailsId", nativeQuery = true)
    Optional<RAFileDetails> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);

    @Query(value = "select distinct(orgnl_file_nm) from RA_RT_FILE_DETAILS where status_cd in (:statusCodes) " +
            "and UPPER(orgnl_file_nm) like UPPER(:searchStr) || '%' and is_active = 1", nativeQuery = true)
    List<String> findByFileSearchStr(@Param("searchStr") String searchStr, List<Integer> statusCodes);

    @Query(value = "select distinct(RA_RT_FILE_ALT_IDS.ALT_ID) from RA_RT_FILE_ALT_IDS, RA_RT_FILE_DETAILS where " +
            "RA_RT_FILE_ALT_IDS.ra_file_details_id = RA_RT_FILE_DETAILS.id and ALT_ID_TYPE='RO_ID' and " +
            " UPPER(ALT_ID) like UPPER(:plmSearchStr) || '%'" +
            " and status_cd in (:statusCodes) and RA_RT_FILE_ALT_IDS.is_active = 1 and RA_RT_FILE_DETAILS.is_active = 1", nativeQuery = true)
    List<String> findByPlmSearchStr(@Param("plmSearchStr") String plmSearchStr, List<Integer> statusCodes);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS, RA_RT_FILE_DETAILS_LOB where " +
            " RA_RT_FILE_DETAILS_LOB.ra_file_details_id = RA_RT_FILE_DETAILS.id " +
            "and UPPER(lob) like '%' || UPPER(:searchStr) || '%' and RA_RT_FILE_DETAILS_LOB.is_active = 1 and RA_RT_FILE_DETAILS.is_active = 1", nativeQuery = true)
    List<RAFileDetails> findByLineOfBusinessSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select * from RA_RT_FILE_DETAILS where UPPER(market) like '%' || UPPER(:searchStr) || '%' and is_active = 1", nativeQuery = true)
    List<RAFileDetails> findByMarketSearchStr(@Param("searchStr") String searchStr);

    @Query(value = "select distinct(market) from RA_RT_FILE_DETAILS where status_cd in (:statusCodes) and is_active = 1", nativeQuery = true)
    List<String> findAllMarkets(List<Integer> statusCodes);

    @Query(value = "select * from RA_RT_FILE_DETAILS where status_cd in (:statusCodes) and MANUAL_ACTN_REQ in (:manualActionRequiredList) and is_active = 1 " +
            "offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findFileDetailsByStatusCodesWithManualActionReqList(List<Integer> statusCodes, List<Integer> manualActionRequiredList, int limit, int offset);

    @Query(value = "select * from RA_RT_FILE_DETAILS where status_cd in (:statusCodes) and is_active = 1 " +
            "offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findFileDetailsByStatusCodes(List<Integer> statusCodes, int limit, int offset);

    @Query(value = "select * from RA_RT_FILE_DETAILS where id = :raFileDetailsId and version = :version", nativeQuery = true)
    Optional<RAFileDetails> findByRAFileDetailsIdByVersion(@Param("raFileDetailsId") Long raFileDetailsId, Long version);
    @Query(value = "select * from RA_RT_FILE_DETAILS where status_cd in (:statusCodes) and is_active = 1 " +
            "and ROWNUM <= :limit for update", nativeQuery = true)
    List<RAFileDetails> findFileDetailsByStatusCodesForUpdate(List<Integer> statusCodes, int limit);

    @Modifying
    @Transactional
    @Query(value = "update RA_RT_FILE_DETAILS set status_cd = :statusCode, last_updt_user_id = :username, last_updt_dt = :lastUpdatedDate " +
            " where id in (:raFileDetailsIdList)", nativeQuery = true)
    void updateRAFileDetailsStatusByIds(List<Long> raFileDetailsIdList, Integer statusCode, String username, Date lastUpdatedDate);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS where RA_RT_FILE_DETAILS.is_active = 1 and " +
            " (COALESCE(:marketList, NULL) is null or market in (:marketList)) " +
            " and (COALESCE(:lineOfBusinessList, NULL) is null or id in (select rLob.ra_file_details_id from RA_RT_FILE_DETAILS_LOB rLob where rLob.is_active = 1 and rLob.lob in (:lineOfBusinessList))) " +
            " and RA_RT_FILE_DETAILS.creat_dt >= :startDate and RA_RT_FILE_DETAILS.creat_dt < :endDate " +
            " and status_cd in (:statusCodes) " +
            " and (COALESCE(:fileNameList, NULL) is null or orgnl_file_nm in (:fileNameList)) " +
            " and (COALESCE(:businessStatusList, NULL) is null or exists (select * from RA_RT_STATUS_CD_MSTR mstr where mstr.is_active = 1 and mstr.status_cd = RA_RT_FILE_DETAILS.status_cd and mstr.bsns_status in (:businessStatusList))) " +
            " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_FILE_DETAILS.id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList))) " +
            " and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_SHEET_DETAILS.is_active = 1 and RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) >= :minSheetCount",
            countQuery="select count(*) from RA_RT_FILE_DETAILS where is_active = 1 and " +
                    " (COALESCE(:marketList, NULL) is null or market in (:marketList)) " +
                    " and (COALESCE(:lineOfBusinessList, NULL) is null or id in (select rLob.ra_file_details_id from RA_RT_FILE_DETAILS_LOB rLob where rLob.is_active = 1 and rLob.lob in (:lineOfBusinessList))) " +
                    " and RA_RT_FILE_DETAILS.creat_dt >= :startDate and RA_RT_FILE_DETAILS.creat_dt < :endDate " +
                    " and status_cd in (:statusCodes) " +
                    " and (COALESCE(:fileNameList, NULL) is null or orgnl_file_nm in (:fileNameList)) " +
                    " and (COALESCE(:businessStatusList, NULL) is null or exists (select * from RA_RT_STATUS_CD_MSTR mstr where mstr.is_active = 1 and mstr.status_cd = RA_RT_FILE_DETAILS.status_cd and mstr.bsns_status in (:businessStatusList))) " +
                    " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_FILE_DETAILS.id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList))) " +
                    " and (select count(*) from RA_RT_SHEET_DETAILS where RA_RT_SHEET_DETAILS.is_active = 1 and RA_RT_FILE_DETAILS.id = RA_RT_SHEET_DETAILS.ra_file_details_id and type in (:types)) >= :minSheetCount",
                    nativeQuery = true)
    Page<RAFileDetails> findRAFileDetailsWithFilters(List<String> fileNameList, List<String> plmTicketIdList, List<String> marketList, List<String> lineOfBusinessList, Date startDate, Date endDate,
                                                     List<Integer> statusCodes, List<String> types, int minSheetCount, List<String> businessStatusList, Pageable pageable);

    @Query(value = "select RA_RT_FILE_DETAILS.* from RA_RT_FILE_DETAILS where RA_RT_FILE_DETAILS.is_active = 1 " +
            " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_FILE_DETAILS.id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS " +
            " where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList))) " +
            " and (COALESCE(:raFileDetailsIdList, NULL) is null or id in (:raFileDetailsIdList))",
            countQuery="select count(*) from RA_RT_FILE_DETAILS where RA_RT_FILE_DETAILS.is_active = 1 " +
                    " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_FILE_DETAILS.id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS " +
                    " where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList))) " +
                    " and (COALESCE(:raFileDetailsIdList, NULL) is null or id in (:raFileDetailsIdList))",
            nativeQuery = true)
    Page<RAFileDetails> findRAFileDetailsWithData(List<Long> raFileDetailsIdList, List<String> plmTicketIdList, Pageable pageable);

}
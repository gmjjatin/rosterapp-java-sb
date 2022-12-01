package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RASheetDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RASheetDetailsRepository extends JpaRepository<RASheetDetails, Long> {
    @Query(value = "select * from RA_RT_SHEET_DETAILS where ra_file_details_id in (:raFileDetailsIds) and type in (:types) and is_active = 1", nativeQuery = true)
    List<RASheetDetails> findRASheetDetailsListForFileIdsList(List<Long> raFileDetailsIds, List<String> types);

    @Query(value = "select * from RA_RT_SHEET_DETAILS where RA_FILE_DETAILS_ID = :raFileDetailsId  and type in (:types) and is_active = 1", nativeQuery = true)
    List<RASheetDetails> getSheetDetailsForAFileId(Long raFileDetailsId, List<String> types);

    @Query(value = "select * from RA_RT_SHEET_DETAILS where RA_FILE_DETAILS_ID = :raFileDetailsId  and is_active = 1", nativeQuery = true)
    List<RASheetDetails> getSheetDetailsForAFileId(Long raFileDetailsId);

    @Query(value = "select ra_rt_sheet_details.* from ra_rt_sheet_details, ra_rt_file_details where ra_rt_sheet_details.ra_file_details_id = ra_rt_file_details.id " +
            "and ra_rt_sheet_details.is_active = 1 and  ra_rt_file_details.status_cd in (:fileStatusCodes) and ra_rt_sheet_details.status_cd in (:sheetStatusCodes)" +
            " and MANUAL_ACTN_REQ in (:manualActionRequiredList)" +
            " and ROWNUM <= :limit order by last_updt_dt for update", nativeQuery = true)
    List<RASheetDetails> getSheetDetailsBasedFileStatusAndSheetStatusCodesForUpdate(List<Integer> fileStatusCodes, List<Integer> sheetStatusCodes, List<Integer> manualActionRequiredList,
                                                                                Integer limit);

    @Modifying
    @Transactional
    @Query(value = "update RA_RT_SHEET_DETAILS set status_cd = :statusCode, last_updt_user_id = :username, last_updt_dt = :lastUpdatedDate " +
            " where id in (:raSheetDetailsIdList)", nativeQuery = true)
    void updateRASheetDetailsStatusByIds(List<Long> raSheetDetailsIdList, Integer statusCode, String username, Date lastUpdatedDate);


    @Query(value = "select * from ra_rt_sheet_details where is_active = 1 and status_cd in (:sheetStatusCodes) " +
            " and ROWNUM <= :limit for update", nativeQuery = true)
    List<RASheetDetails> getSheetDetailsBasedOnSheetStatusCodesForUpdate(List<Integer> sheetStatusCodes, Integer limit);

    @Query(value = "select * from ra_rt_sheet_details where is_active = 1 and status_cd in (:sheetStatusCodes) " +
            " and ROWNUM <= :limit and VLDTN_FILE_ID IS NOT NULL order by last_updt_dt for update", nativeQuery = true)
    List<RASheetDetails> getSheetDetailsBasedOnSheetStatusCodesWithFileIdForUpdate(List<Integer> sheetStatusCodes, Integer limit);

}

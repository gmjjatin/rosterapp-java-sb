package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RASheetDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface RASheetDetailsRepository extends JpaRepository<RASheetDetails, Long> {
    @Query(value = "select * from RA_RT_SHEET_DETAILS where ra_file_details_id in (:raFileDetailsIds) and is_active = 1", nativeQuery = true)
    List<RASheetDetails> findRASheetDetailsListForFileIdsList(List<Long> raFileDetailsIds);

    @Query(value = "select * from RA_RT_SHEET_DETAILS where RA_FILE_DETAILS_ID = :raFileDetailsId and is_active = 1", nativeQuery = true)
    List<RASheetDetails> getSheetDetailsForAFileId(Long raFileDetailsId);

    @Modifying
    @Transactional
    @Query(value = "update RA_RT_SHEET_DETAILS set status_cd = :statusCode where id = :raSheetDetailsId", nativeQuery = true)
    void updateRASheetDetailsStatus(Long raSheetDetailsId, Integer statusCode);
}

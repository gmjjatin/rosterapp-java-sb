package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RASheetDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RASheetDetailsRepository extends JpaRepository<RASheetDetails, Long> {
    @Query(value = "select * from RA_RT_SHEET_DETAILS where ra_file_details_id in (:raFileDetailsIds)", nativeQuery = true)
    List<RASheetDetails> findRASheetDetailsListForFileIdsList(List<Long> raFileDetailsIds);

    @Query(value = "select * from RA_RT_SHEET_DETAILS where RA_FILE_DETAILS_ID = :fileId", nativeQuery = true)
    List<RASheetDetails> getSheetDetails(Long fileId);
}

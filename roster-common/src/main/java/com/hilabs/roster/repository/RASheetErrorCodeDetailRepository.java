package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RASheetErrorCodeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RASheetErrorCodeDetailRepository extends JpaRepository<RASheetErrorCodeDetails, Long> {
    @Query(value = "select * from RA_SHEET_ERROR_CODE_DETAILS where ra_sheet_details_id = :raSheetDetailsId and is_active = 1",
            nativeQuery = true)
    List<RASheetErrorCodeDetails> findByRASheetDetailsId(@Param("raSheetDetailsId") Long raSheetDetailsId);
}

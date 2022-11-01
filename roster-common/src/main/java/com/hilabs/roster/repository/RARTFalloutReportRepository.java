package com.hilabs.roster.repository;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RARTFalloutReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RARTFalloutReportRepository extends JpaRepository<RARTFalloutReport, Long> {
    @Query(value = "select rule_ctgry_stg as category, err_type as errorType, count(*) as count from RA_RT_FALLOUT_REPORT " +
            "where ra_sheet_details_id = :raSheetDetailsId group by rule_ctgry_stg, err_type",
            nativeQuery = true)
    List<RAFalloutErrorInfo> getRAFalloutErrorInfoList(long raSheetDetailsId);

    @Query(value = "select count(RA_ROW_ID) from RA_RT_FALLOUT_REPORT where ra_sheet_details_id = :raSheetDetailsId " +
            "and rule_ctgry_stg = :stage",
            nativeQuery = true)
    Integer countRAFalloutErrorInfo(long raSheetDetailsId, String stage);
}
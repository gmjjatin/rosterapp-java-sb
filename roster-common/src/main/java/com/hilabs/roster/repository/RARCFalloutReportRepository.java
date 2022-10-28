package com.hilabs.roster.repository;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RARCFalloutReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RARCFalloutReportRepository extends JpaRepository<RARCFalloutReport, Long> {
    @Query(value = "select rule_ctgry_stage as category, err_type as errorType count(*) as count from RA_RC_FALLOUT_REPORT " +
            "where ra_sheet_details_id = :raSheetDetailsId group by rule_ctgry_stage, err_type",
            nativeQuery = true)
    List<RAFalloutErrorInfo> getRAFalloutErrorInfoList(long raSheetDetailsId);
}
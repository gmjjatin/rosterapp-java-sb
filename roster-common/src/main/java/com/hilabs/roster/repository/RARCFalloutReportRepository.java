package com.hilabs.roster.repository;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RARCFalloutReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RARCFalloutReportRepository extends JpaRepository<RARCFalloutReport, Long> {
    @Query(value = "select err_type as errorType, err_code as errorCode, err_dscrptn as errorDescription," +
            " count(*) as count from RA_RC_FALLOUT_REPORT where ra_sheet_details_id = :raSheetDetailsId \n" +
            "group by err_type, err_code, err_dscrptn",
            nativeQuery = true)
    List<RAFalloutErrorInfo> getRAFalloutErrorInfoList(long raSheetDetailsId);
}
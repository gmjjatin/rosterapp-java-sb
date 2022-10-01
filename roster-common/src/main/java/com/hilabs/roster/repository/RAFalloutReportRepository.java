package com.hilabs.roster.repository;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RAFalloutReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RAFalloutReportRepository extends JpaRepository<RAFalloutReport, Long> {
//    @Query(value = "select * from ra_fallout_report where ra_sheet_details_id = :raSheetDetailsId",
//            nativeQuery = true)
//    List<RAFalloutReport> getRASheetFalloutReportList(long raSheetDetailsId);

    @Query(value = "select err_type as errorType, err_code as errorCode, err_dscrptn as errorDescription, count(*) as count from ra_fallout_report where ra_sheet_details_id = :raSheetDetailsId \n" +
            "group by err_type, err_code, err_dscrptn",
            nativeQuery = true)
    List<RAFalloutErrorInfo> getRAFalloutErrorInfoList(long raSheetDetailsId);
}

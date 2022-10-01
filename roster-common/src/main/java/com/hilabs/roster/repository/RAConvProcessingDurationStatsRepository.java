package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAConvProcessingDurationStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface RAConvProcessingDurationStatsRepository extends JpaRepository<RAConvProcessingDurationStats, Long> {
    @Query(value = "select * from ra_conv_processing_duration_stats where ra_sheet_details_id = :raSheetDetailsId",
            nativeQuery = true)
    List<RAConvProcessingDurationStats> getRAConvProcessingDurationStatsList(long raSheetDetailsId);
}

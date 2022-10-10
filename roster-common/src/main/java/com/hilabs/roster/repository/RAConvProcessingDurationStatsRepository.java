package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RARTConvProcessingDurationStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface RAConvProcessingDurationStatsRepository extends JpaRepository<RARTConvProcessingDurationStats, Long> {
    @Query(value = "select * from RA_RT_CONV_PROCESSING_DURATION_STATS where ra_sheet_deatils_id = :raSheetDetailsId",
            nativeQuery = true)
    List<RARTConvProcessingDurationStats> getRAConvProcessingDurationStatsList(long raSheetDetailsId);
}
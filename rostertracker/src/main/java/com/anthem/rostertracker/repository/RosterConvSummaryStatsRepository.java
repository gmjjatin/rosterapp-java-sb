//package com.anthem.rostertracker.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//
//public interface RosterConvSummaryStatsRepository extends JpaRepository<RosterConvSummaryStats, Long> {
//    @Query(value = "select * from ros_conv_summary_stats where roster_file_details_id in (:rosterFileIds)", nativeQuery = true)
//    List<RosterConvSummaryStats> getRosterConvSummaryStatsListFromIds(List<Long> rosterFileIds);
//
//    @Query(value = "select * from ros_conv_summary_stats where roster_file_details_id = :rosterFileId", nativeQuery = true)
//    RosterConvSummaryStats getRosterConvSummaryStatsFromRosterFileId(Long rosterFileId);
//}

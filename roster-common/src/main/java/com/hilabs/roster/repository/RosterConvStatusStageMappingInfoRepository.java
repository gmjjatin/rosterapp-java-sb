package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAConvStatusStageMappingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface RosterConvStatusStageMappingInfoRepository extends JpaRepository<RAConvStatusStageMappingInfo, Long> {
    @Query(value = "select * from ra_conv_status_stage_mappings", nativeQuery = true)
    List<RAConvStatusStageMappingInfo> findAll();
}

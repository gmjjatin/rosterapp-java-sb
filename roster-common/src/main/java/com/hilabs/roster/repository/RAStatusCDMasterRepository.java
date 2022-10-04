package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAStatusCDMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RAStatusCDMasterRepository extends JpaRepository<RAStatusCDMaster, Long> {
    @Query(value = "select * from RA_RT_STATUS_CD_MSTR where stage_nm = :stage", nativeQuery = true)
    List<RAStatusCDMaster> getRAStatusCDMasterListForStage(@Param("stage") String stage);

    @Query(value = "select * from RA_RT_STATUS_CD_MSTR where status_cd = :statusCode", nativeQuery = true)
    Optional<RAStatusCDMaster> getRAStatusCDMasterListForCode(@Param("statusCode") Integer statusCode);
}

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

    @Query(value = "select * from RA_RT_STATUS_CD_MSTR where status_cd = :statusCode order by creat_dt desc fetch next 1 rows only", nativeQuery = true)
    Optional<RAStatusCDMaster> getRAStatusCDMasterListForCode(@Param("statusCode") Integer statusCode);

    @Query(value = "select * from RA_RT_STATUS_CD_MSTR where status_cd < 100 and is_active = 1  order by status_cd", nativeQuery = true)
    List<RAStatusCDMaster> getAllFileRAStatusCDMasterList();

    @Query(value = "select * from RA_RT_STATUS_CD_MSTR where status_cd >= 100 and is_active = 1 order by status_cd", nativeQuery = true)
    List<RAStatusCDMaster> getAllSheetRAStatusCDMasterList();

    @Query(value = "select * from RA_RT_STATUS_CD_MSTR where status_cd in (:statusCodes)" +
            " and is_active = 1 order by status_cd", nativeQuery = true)
    List<RAStatusCDMaster> getSheetRAStatusCDMasterList(List<Integer> statusCodes);
}

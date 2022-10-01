package com.hilabs.rapipeline.repository;

import com.hilabs.roster.entity.RAPlmRoFileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RAPlmRoFileDataRepository extends JpaRepository<RAPlmRoFileData, Long> {
    @Query(value = "select * from ra_plm_ro_file_data where ra_file_prcs_stts = 'NEW'", nativeQuery = true)
    List<RAPlmRoFileData> getNewRAPlmRoFileDataList();

    @Modifying
    @Transactional
    @Query(value = "update ra_plm_ro_file_data set ra_file_prcs_stts = :status," +
            " last_updt_dt = sysdate " +
            "where ra_plm_ro_file_data_id = :raPlmRoFileDataId", nativeQuery = true)
    void updateRAPlmRoFileDataStatus(@Param("raPlmRoFileDataId") long raPlmRoFileDataId, @Param("status") String status);
}
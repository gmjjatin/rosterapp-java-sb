package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAPlmRoFileData;
import com.hilabs.roster.entity.RAPlmRoProfData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RAPlmRoProfDataRepository extends JpaRepository<RAPlmRoProfData, Long> {
    @Query(value = "select * from ra_plm_ro_prof_data where ra_plm_ro_prof_data_id in (:idList)", nativeQuery = true)
    List<RAPlmRoProfData> findRAPlmRoProfDataByIds(@Param("idList") List<Long> idList);
}
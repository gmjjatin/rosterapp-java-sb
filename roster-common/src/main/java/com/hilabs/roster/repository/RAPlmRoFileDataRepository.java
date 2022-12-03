package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAPlmRoFileData;
import com.hilabs.roster.entity.RAPlmRoProfData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
//NEW  + N
//Y
public interface RAPlmRoFileDataRepository extends JpaRepository<RAPlmRoFileData, Long> {
    @Query(value = "select ra_plm_ro_file_data.* from ra_plm_ro_file_data, ra_plm_ro_prof_data where ra_plm_ro_file_data.ra_plm_ro_prof_data_id = ra_plm_ro_prof_data.ra_plm_ro_prof_data_id and " +
            " ra_file_prcs_stts = :status and ra_plm_ro_prof_data.IS_FILE_DLD_COMP='DL' " +
            "and ROWNUM <= :limit for update", nativeQuery = true)
    List<RAPlmRoFileData> getNewRAPlmRoFileDataListWithStatus(@Param("status") String status, @Param("limit") int limit);

    @Modifying
    @Transactional
    @Query(value = "update ra_plm_ro_file_data set ra_file_prcs_stts = :status where ra_plm_ro_file_data_id in (:raPlmRoFileDataIdList)", nativeQuery = true)
    void updateRAPlmRoFileDataListWithStatus(String status, List<Long> raPlmRoFileDataIdList);

//    @Query(value = "select * from ra_plm_ro_file_data where UPPER(reprcs_yn) like 'Y%' and ra_file_prcs_stts = :status", nativeQuery = true)
//    List<RAPlmRoFileData> getReprocessRAPlmRoFileDataListWithStatus(@Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "update ra_plm_ro_file_data set ra_file_prcs_stts = :status, reprcs_yn = :reProcess," +
            " last_updt_dt = sysdate " +
            "where ra_plm_ro_file_data_id = :raPlmRoFileDataId", nativeQuery = true)
    void updateRAPlmRoFileDataStatus(@Param("raPlmRoFileDataId") long raPlmRoFileDataId, @Param("status") String status,
                                     @Param("reProcess") String reProcess);

    @Query(value = "select ra_plm_ro_file_data.* from ra_plm_ro_file_data, ra_plm_ro_prof_data where ra_plm_ro_file_data.ra_plm_ro_prof_data_id = ra_plm_ro_prof_data.ra_plm_ro_prof_data_id and " +
            " ra_file_prcs_stts = :status and ra_plm_ro_prof_data.IS_FILE_DLD_COMP='DL' " +
            "and ROWNUM <= :limit for update", nativeQuery = true)
    List<RAPlmRoFileData> getRAPlmRoFileDataList(@Param("status") String status, @Param("limit") int limit);

    @Query(value = "select ra_plm_ro_file_data.* from ra_plm_ro_file_data, ra_plm_ro_prof_data where ra_plm_ro_file_data.ra_plm_ro_prof_data_id = ra_plm_ro_prof_data.ra_plm_ro_prof_data_id " +
            " and ra_plm_ro_prof_data.ro_id = :roId",
            countQuery="select count(*) from ra_plm_ro_prof_data where ro_id = :roId", nativeQuery = true)
    Page<RAPlmRoFileData> findRAPlmRoFileDataList(@Param("roId") String roId, Pageable pageable);
}
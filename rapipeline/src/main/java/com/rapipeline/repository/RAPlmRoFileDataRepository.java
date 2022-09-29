package com.rapipeline.repository;

import com.rapipeline.dto.RAFileMetaData;
import com.rapipeline.entity.RAPlmRoFileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface RAPlmRoFileDataRepository extends JpaRepository<RAPlmRoFileData, Long> {
//    @Query(value = "select ra_plm_ro_prof_data.ra_plm_ro_prof_data_id as raPlmRoProfDataId, ro_id as roId," +
//            " t_case_id as tCaseId, racd_id as racdId, ract_id as ractId, racf_id as racfId, eid," +
//            " tax_id as taxId, org_nm as orgName, cnt_state as cntState, plm_ntwk as plmNetwork," +
//            " corp_recipt_dt as corporateReceiptDate, ra_plm_ro_file_data_id as raPlmRoFileDataId, file_nm as fileName," +
//            " dcn_id as dcnId, file_size as fileSize, ra_file_prcs_stts as raFileProcessingStatus," +
//            " deposit_dt as depositDate, f_docnum as fileDocumentNumber from ra_plm_ro_file_data, ra_plm_ro_prof_data " +
//            "where ra_plm_ro_file_data.ra_plm_ro_prof_data_id = ra_plm_ro_file_data.ra_plm_ro_prof_data_id " +
//            "and ra_file_prcs_stts = 'NEW'", nativeQuery = true)
//    List<RAFileMetaData> getUnIngestedRAFileMetaDataDetails();

    @Query(value = "select * from ra_plm_ro_file_data where ra_file_prcs_stts = 'NEW'", nativeQuery = true)
    List<RAPlmRoFileData> getNewRAPlmRoFileDataList();

    @Modifying
    @Transactional
    @Query(value = "update ra_plm_ro_file_data set ra_file_prcs_stts = :status," +
            " last_updt_dt = sysdate " +
            "where ra_plm_ro_file_data_id = :raPlmRoFileDataId", nativeQuery = true)
    void updateRAPlmRoFileDataStatus(@Param("raPlmRoFileDataId") long raPlmRoFileDataId, @Param("status") String status);
}
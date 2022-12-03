package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.roster.entity.RASheetErrorCodeDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RASheetErrorCodeDetailRepository extends JpaRepository<RASheetErrorCodeDetails, Long> {
    @Query(value = "select * from RA_SHEET_ERROR_CODE_DETAILS where ra_sheet_details_id = :raSheetDetailsId and is_active = 1",
            nativeQuery = true)
    List<RASheetErrorCodeDetails> findByRASheetDetailsId(@Param("raSheetDetailsId") Long raSheetDetailsId);

    @Query(value = "select RA_SHEET_ERROR_CODE_DETAILS.* from RA_SHEET_ERROR_CODE_DETAILS, RA_RT_SHEET_DETAILS where RA_RT_SHEET_DETAILS.is_active = 1 " +
            " and RA_SHEET_ERROR_CODE_DETAILS.is_active = 1 " +
            " and RA_RT_SHEET_DETAILS.id = RA_SHEET_ERROR_CODE_DETAILS.RA_SHEET_DETAILS_ID " +
            " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_SHEET_DETAILS.ra_file_details_id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList)))",
            countQuery="select count(*) from RA_SHEET_ERROR_CODE_DETAILS, RA_RT_SHEET_DETAILS where RA_RT_SHEET_DETAILS.is_active = 1" +
                    " and RA_SHEET_ERROR_CODE_DETAILS.is_active = 1 " +
                    " and RA_RT_SHEET_DETAILS.id = RA_SHEET_ERROR_CODE_DETAILS.RA_SHEET_DETAILS_ID " +
                    " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_SHEET_DETAILS.ra_file_details_id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList)))",
            nativeQuery = true)
    Page<RASheetErrorCodeDetails> findRASheetErrorCodeDetailsData(List<String> plmTicketIdList, Pageable pageable);
}

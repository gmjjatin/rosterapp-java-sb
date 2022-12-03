package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RASheetErrorCodeDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RAFileErrorCodeDetailRepository extends JpaRepository<RAFileErrorCodeDetails, Long> {
    @Query(value = "select * from RA_FILE_ERROR_CODE_DETAILS where ra_file_details_id = :raFileDetailsId",
            nativeQuery = true)
    List<RAFileErrorCodeDetails> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);

    @Query(value = "select RA_FILE_ERROR_CODE_DETAILS.* from RA_FILE_ERROR_CODE_DETAILS, RA_RT_FILE_DETAILS where RA_RT_FILE_DETAILS.is_active = 1 " +
            " and RA_FILE_ERROR_CODE_DETAILS.is_active = 1 " +
            " and RA_RT_FILE_DETAILS.id = RA_FILE_ERROR_CODE_DETAILS.RA_FILE_DETAILS_ID " +
            " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_FILE_DETAILS.id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList)))",
            countQuery="select count(*) from RA_FILE_ERROR_CODE_DETAILS, RA_RT_FILE_DETAILS where RA_RT_FILE_DETAILS.is_active = 1" +
                    " and RA_FILE_ERROR_CODE_DETAILS.is_active = 1 " +
                    " and RA_RT_FILE_DETAILS.id = RA_FILE_ERROR_CODE_DETAILS.RA_FILE_DETAILS_ID " +
                    " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_FILE_DETAILS.id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList)))",
            nativeQuery = true)
    Page<RAFileErrorCodeDetails> findRAFileErrorCodeDetailsData(List<String> plmTicketIdList, Pageable pageable);
}


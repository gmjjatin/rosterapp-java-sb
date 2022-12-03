package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RARCRosterISFMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface RARCRosterISFMapRepository extends CrudRepository<RARCRosterISFMap, Long> {
    @Query(value = "select * from RA_RC_ROSTER_ISF_MAP where RA_SHEET_DETAILS_ID = :raSheetDetailsId and is_active = 1", nativeQuery = true)
    List<RARCRosterISFMap> getRARCRosterISFMapList(Long raSheetDetailsId);

    @Modifying
    @Transactional
    @Query(value = "update RA_RC_ROSTER_ISF_MAP set is_active = :isActive, last_updt_user_id = :username, last_updt_dt = :lastUpdatedDate " +
            "where id in (:ids)", nativeQuery = true)
    void updateIsActiveForRARCRosterISFMap(List<Long> ids, Integer isActive, String username, Date lastUpdatedDate);

    @Query(value = "select count(*) from RA_RC_ROSTER_ISF_MAP where RA_SHEET_DETAILS_ID = :raSheetDetailsId and is_active = 1", nativeQuery = true)
    int countMappingCountForSheetDetailsId(Long raSheetDetailsId);

    @Query(value = "select RA_RC_ROSTER_ISF_MAP.* from RA_RC_ROSTER_ISF_MAP, RA_RT_SHEET_DETAILS where RA_RT_SHEET_DETAILS.is_active = 1 " +
            " and (RA_RC_ROSTER_ISF_MAP.is_active = 1 or RA_RC_ROSTER_ISF_MAP.last_updt_user_id != 'SYSTEM')" +
            " and RA_RT_SHEET_DETAILS.id = RA_RC_ROSTER_ISF_MAP.RA_SHEET_DETAILS_ID " +
            " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_SHEET_DETAILS.ra_file_details_id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList)))",
            countQuery="select count(*) from RA_RC_ROSTER_ISF_MAP, RA_RT_SHEET_DETAILS where RA_RT_SHEET_DETAILS.is_active = 1" +
                    " and RA_RC_ROSTER_ISF_MAP.is_active = 1 " +
                    " and RA_RT_SHEET_DETAILS.id = RA_RC_ROSTER_ISF_MAP.RA_SHEET_DETAILS_ID " +
                    " and (COALESCE(:plmTicketIdList, NULL) is null or RA_RT_SHEET_DETAILS.ra_file_details_id in (select ra_file_details_id from RA_RT_FILE_ALT_IDS where RA_RT_FILE_ALT_IDS.is_active = 1 and ALT_ID_TYPE='RO_ID' and ALT_ID in (:plmTicketIdList)))",
            nativeQuery = true)
    Page<RARCRosterISFMap> findRARCRosterISFMapData(List<String> plmTicketIdList, Pageable pageable);
}

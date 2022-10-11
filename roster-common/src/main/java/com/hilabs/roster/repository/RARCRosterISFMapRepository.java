package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.roster.entity.RASheetDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RARCRosterISFMapRepository extends CrudRepository<RARCRosterISFMap, Long> {
    @Query(value = "select * from RA_RC_ROSTER_ISF_MAP where RA_SHEET_DETAILS_ID = :raSheetDetailsId and is_active = 1", nativeQuery = true)
    List<RARCRosterISFMap> getRARCRosterISFMapList(Long raSheetDetailsId);

    @Modifying
    @Transactional
    @Query(value = "update RA_RC_ROSTER_ISF_MAP set is_active = :isActive where id in (:ids)", nativeQuery = true)
    void updateIsActiveForRARCRosterISFMap(List<Long> ids, Integer isActive);

    @Query(value = "select count(*) from RA_RC_ROSTER_ISF_MAP where RA_SHEET_DETAILS_ID = :raSheetDetailsId and is_active = 1", nativeQuery = true)
    int countMappingCountForSheetDetailsId(Long raSheetDetailsId);
}

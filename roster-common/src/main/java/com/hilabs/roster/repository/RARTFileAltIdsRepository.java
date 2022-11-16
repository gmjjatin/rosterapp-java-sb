package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RARTFileAltIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RARTFileAltIdsRepository extends JpaRepository<RARTFileAltIds, Long> {
    @Query(value = "select * from RA_RT_FILE_ALT_IDS where ra_file_details_id in (:raFileDetailsIdList)",
            nativeQuery = true)
    List<RARTFileAltIds> findByRAFileDetailsIdList(@Param("raFileDetailsIdList") List<Long> raFileDetailsIdList);

    @Query(value = "select * from RA_RT_FILE_ALT_IDS where ra_file_details_id in (:raFileDetailsIdList) and alt_id_type = :altIdType",
            nativeQuery = true)
    List<RARTFileAltIds> findByRAFileDetailsIdList(@Param("raFileDetailsIdList") List<Long> raFileDetailsIdList,
                                                   String altIdType);
}


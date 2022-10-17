package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAFileDetailsLob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RAFileDetailsLobRepository extends CrudRepository<RAFileDetailsLob, Long> {
    @Query(value = "select distinct(lob) from RA_RT_FILE_DETAILS_LOB", nativeQuery = true)
    List<String> findAllLineOfBusinesses();

    @Query(value = "select * from RA_RT_FILE_DETAILS_LOB where ra_file_details_id in (:raFileDetailsIdList) and is_active = 1", nativeQuery = true)
    List<RAFileDetailsLob> findRAFileDetailsLobByFileId(List<Long> raFileDetailsIdList);
}

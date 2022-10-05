package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RAFileErrorCodeDetailRepository extends JpaRepository<RAFileErrorCodeDetails, Long> {
    @Query(value = "select * from DART_RA_FILE_ERROR_CODE_DETAILS where ra_file_details_id = :raFileDetailsId",
            nativeQuery = true)
    List<RAFileErrorCodeDetails> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);
}


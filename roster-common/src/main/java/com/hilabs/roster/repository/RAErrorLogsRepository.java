package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAErrorLogs;
import com.hilabs.roster.entity.RAFileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface RAErrorLogsRepository extends JpaRepository<RAErrorLogs, Long> {
    @Query(value = "select * from RA_ERROR_LOGS where ra_file_details_id = :raFileDetailsId",
            nativeQuery = true)
    List<RAErrorLogs> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);
}


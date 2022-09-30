package com.rapipeline.repository;

import com.rapipeline.entity.RASystemErrors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface RASystemErrorsRepository extends JpaRepository<RASystemErrors, Long> {
    @Modifying
    @Query(value = "insert into ra_system_errors(ra_file_details_id, last_stage, last_status, error_category," +
            " error_description, error_stack_trace, creat_user_id, last_updt_user_id) " +
            "VALUES (:raFileDetailsId, :lastStage, :lastStatus,  :errorCategory, :errorDescription," +
            " :errorStackTrace, :createdUserId, :lastUpdatedUserId)", nativeQuery = true)
    @Transactional
    void insertRASystemErrors(@Param("raFileDetailsId") Long raFileDetailsId,
                             @Param("lastStage") String lastStage,
                             @Param("lastStatus") Integer lastStatus,
                             @Param("errorCategory") String errorCategory,
                             @Param("errorDescription") String errorDescription,
                             @Param("errorStackTrace") String errorStackTrace,
                             @Param("createdUserId") Long createdUserId,
                             @Param("lastUpdatedUserId") Long lastUpdatedUserId);
}

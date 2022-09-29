package com.rapipeline.repository;

import com.rapipeline.entity.RAFileDetails;
import com.rapipeline.entity.RAFileXStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RAFileXStatusRepository extends JpaRepository<RAFileXStatus, Long> {
    @Modifying
    @Query(value = "insert into ra_file_x_status(ra_file_details_id, status_code) " +
            "values (:raFileDetailsId, :statusCode)", nativeQuery = true)
    @Transactional
    void insertRAFileXStatus(@Param("raFileDetailsId") Long raFileDetailsId,
                             @Param("statusCode") int statusCode);

    @Query(value = "select * from ra_file_x_status where ra_file_details_id = :raFileDetailsId",
            nativeQuery = true)
    Optional<RAFileXStatus> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);

    @Modifying
    @Transactional
    @Query(value = "update ra_file_x_status set status_code = :statusCode, last_updt_dt = sysdate " +
            "where ra_file_details_id = :raFileDetailsId", nativeQuery = true)
    void updateRAFileXStatus(@Param("raFileDetailsId") Long raFileDetailsId,
                                     @Param("statusCode") int statusCode);
}

package com.hilabs.roster.repository;

import com.hilabs.roster.entity.DartRaErrorCodeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DartRaErrorCodeDetailsRepository extends JpaRepository<DartRaErrorCodeDetails, Long> {
    @Query(value = "select * from DART_RA_ERROR_CODE_DETAILS where ERROR_CODE = :errorCode",
            nativeQuery = true)
    List<DartRaErrorCodeDetails> findByErrorCode(String errorCode);
}

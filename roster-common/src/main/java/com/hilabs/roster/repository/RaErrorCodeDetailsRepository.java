package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RaErrorCodeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RaErrorCodeDetailsRepository extends JpaRepository<RaErrorCodeDetails, Long> {
    @Query(value = "select * from RA_ERROR_CODE_DETAILS where ERR_CD = :errorCode and is_active = 1",
            nativeQuery = true)
    List<RaErrorCodeDetails> findByErrorCode(String errorCode);
}

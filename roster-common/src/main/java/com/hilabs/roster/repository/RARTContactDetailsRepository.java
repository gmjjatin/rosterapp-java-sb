package com.hilabs.roster.repository;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RARTContactDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RARTContactDetailsRepository extends JpaRepository<RARTContactDetails, Long> {

    @Query(value = "select * from RA_RT_CONTACT_DETAILS where ra_file_details_id = :raFileDetailsId",
            nativeQuery = true)
    List<RARTContactDetails> findRARTContactDetailsByFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);
}


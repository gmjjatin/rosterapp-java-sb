package com.rapipeline.repository;

import com.rapipeline.entity.RAFileMetaDataDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface RAFileMetaDataDetailsRepository extends JpaRepository<RAFileMetaDataDetails, Long> {
    @Query(value = "select * from ra_file_meta_data_details where ingestion_status = 0 and retry_no < :maxRetryNo", nativeQuery = true)
    List<RAFileMetaDataDetails> getUnIngestedRAFileMetaDataDetails(@Param("maxRetryNo") int maxRetryNo);

    @Modifying
    @Transactional
    @Query(value = "update ra_file_meta_data_details set ingestion_status = :ingestionStatus, last_updt_dt = sysdate where id = :id", nativeQuery = true)
    void updateStatusForRAFileMetaDataDetails(@Param("id") long id, @Param("ingestionStatus") int ingestionStatus);

    @Modifying
    @Transactional
    @Query(value = "update ra_file_meta_data_details set retry_no = retry_no + 1, last_updt_dt = sysdate " +
            "where id = :id", nativeQuery = true)
    void incrementRetryNoForRAFileMetaDataDetails(@Param("id") long id);
}
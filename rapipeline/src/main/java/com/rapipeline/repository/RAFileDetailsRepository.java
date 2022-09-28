package com.rapipeline.repository;

import com.rapipeline.entity.RAFileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface RAFileDetailsRepository extends JpaRepository<RAFileDetails, Long> {
    @Modifying
    @Query(value = "insert into ra_file_details(ra_prov_details_id, orgnl_file_nm, stndrdzd_file_nm, plm_ticket_id, file_location, file_system, creat_user_id, last_updt_user_id) " +
            "VALUES (:raProvDetailsId, :originalFileName, :standardizedFileName," +
            " :plmTicketId, :fileLocation, :fileSystem, :createdUserId, :lastUpdateUserId)", nativeQuery = true)
    @Transactional
    void insertRAFileDetails(@Param("raProvDetailsId") Long raProvDetailsId,
                             @Param("originalFileName") String originalFileName,
                             @Param("standardizedFileName") String standardizedFileName,
                             @Param("plmTicketId") String plmTicketId,
                             @Param("fileLocation") String fileLocation,
                             @Param("fileSystem") String fileSystem,
                             @Param("createdUserId") Long createdUserId,
                             @Param("lastUpdateUserId") Long lastUpdateUserId);
}
package com.rapipeline.repository;

import com.rapipeline.entity.RAFileDetails;
import com.rapipeline.entity.RAProvDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    //TODO is fileName unique
    @Query(value = "select * from ra_file_details where orgnl_file_nm = :fileName order by creat_dt desc fetch next 1 rows only",
            nativeQuery = true)
    Optional<RAFileDetails> findByFileName(@Param("fileName") String fileName);
}
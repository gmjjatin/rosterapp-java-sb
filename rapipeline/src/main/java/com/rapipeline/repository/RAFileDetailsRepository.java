package com.rapipeline.repository;

import com.rapipeline.entity.RAFileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RAFileDetailsRepository extends JpaRepository<RAFileDetails, Long> {
    @Modifying
    @Query(value = "insert into ra_file_details(ra_prov_details_id, market, lob,  orgnl_file_nm, stndrdzd_file_nm, plm_ticket_id, file_location, file_system, creat_user_id, last_updt_user_id) " +
            "VALUES (:raProvDetailsId, :market, :lob, :originalFileName, :standardizedFileName," +
            " :plmTicketId, :fileLocation, :fileSystem, :createdUserId, :lastUpdateUserId)", nativeQuery = true)
    @Transactional
    void insertRAFileDetails(@Param("raProvDetailsId") Long raProvDetailsId,
                             @Param("market") String market,
                             @Param("lob") String lob,
                             @Param("originalFileName") String originalFileName,
                             @Param("standardizedFileName") String standardizedFileName,
                             @Param("plmTicketId") String plmTicketId,
                             @Param("fileLocation") String fileLocation,
                             @Param("fileSystem") String fileSystem,
                             @Param("createdUserId") Long createdUserId,
                             @Param("lastUpdateUserId") Long lastUpdateUserId);

    @Modifying
    @Transactional
    @Query(value = "update ra_file_details set ra_prov_details_id = :raProvDetailsId, market = :market, lob = :lob," +
            " orgnl_file_nm = :originalFileName, stndrdzd_file_nm = :standardizedFileName, plm_ticket_id = :plmTicketId," +
            " file_location = :fileLocation, file_system = :fileSystem, last_updt_user_id = :lastUpdateUserId," +
            " last_updt_dt = sysdate " +
            "where id = :raProvDetailsId", nativeQuery = true)
    void updateRAFileDetails(@Param("raProvDetailsId") Long raProvDetailsId,
                             @Param("market") String market,
                             @Param("lob") String lob,
                             @Param("originalFileName") String originalFileName,
                             @Param("standardizedFileName") String standardizedFileName,
                             @Param("plmTicketId") String plmTicketId,
                             @Param("fileLocation") String fileLocation,
                             @Param("fileSystem") String fileSystem,
                             @Param("lastUpdateUserId") Long lastUpdateUserId);

    //TODO is fileName unique
    @Query(value = "select * from ra_file_details where orgnl_file_nm = :fileName order by creat_dt desc fetch next 1 rows only",
            nativeQuery = true)
    Optional<RAFileDetails> findByFileName(@Param("fileName") String fileName);

    @Query(value = "select * from ra_file_details where id = :raFileDetailsId", nativeQuery = true)
    Optional<RAFileDetails> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);
}
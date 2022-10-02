package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAFileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RAFileDetailsRepository extends CrudRepository<RAFileDetails, Long> {

    @Modifying
    @Transactional
    @Query(value = "update RA_RT_FILE_DETAILS set ra_provider_details_id = :raProvDetailsId, market = :market, lob = :lob," +
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
    @Query(value = "select * from RA_RT_FILE_DETAILS where orgnl_file_nm = :fileName order by creat_dt desc fetch next 1 rows only",
            nativeQuery = true)
    Optional<RAFileDetails> findByFileName(@Param("fileName") String fileName);

    @Query(value = "select * from RA_RT_FILE_DETAILS where id = :raFileDetailsId", nativeQuery = true)
    Optional<RAFileDetails> findByRAFileDetailsId(@Param("raFileDetailsId") Long raFileDetailsId);

    //TODO
//    @Query(value = "select * from RA_RT_FILE_DETAILS where ra_provider_details_id in (:raProvDetailsIds) " +
//            "and creat_dt >= :startDate and creat_dt < :endDate order by creat_dt desc offset :offset rows fetch next :limit rows only", nativeQuery = true)
//    List<RAFileDetails> findRAFileDetailsListBetweenDatesFromRAProvDetailsIds(Date startDate, Date endDate, List<Long> raProvDetailsIds, @Param("limit") int limit, @Param("offset") int offset);

    //TODO
    @Query(value = "select * from RA_RT_FILE_DETAILS where ra_provider_details_id in (:raProvDetailsIds) offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findRAFileDetailsListBetweenDatesFromRAProvDetailsIds(List<Long> raProvDetailsIds, @Param("limit") int limit, @Param("offset") int offset);
}

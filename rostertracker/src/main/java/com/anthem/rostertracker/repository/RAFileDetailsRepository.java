package com.anthem.rostertracker.repository;

import com.anthem.rostertracker.entity.RAFileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RAFileDetailsRepository extends JpaRepository<RAFileDetails, Long> {

//    @Query(value = "select distinct(ra_prov_details_id) from ra_file_details where created_date >= :startDate and created_date < :endDate" +
//            " order by ra_prov_details_id limit :limit offset :offset", nativeQuery = true)
//    List<Long> findRAProvDetailsIdsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
//                                                @Param("limit") int limit, @Param("offset") int offset);
//
//    @Query(value = "select distinct(ra_prov_details_id) from ra_file_details, ros_setup_details where created_date >= :startDate " +
//            "and created_date < :endDate and ros_setup_details.market = :market and ros_setup_details.state = :state and ra_file_details.ra_prov_details_id = ros_setup_details.id" +
//            " order by ra_prov_details_id limit :limit offset :offset", nativeQuery = true)
//    List<Long> findRosterSetUpIdsBetweenDatesForMarketAndState(@Param("market") String market, @Param("lineOfBusiness") String lineOfBusiness);
//
//    @Query(value = "select distinct(ra_prov_details_id) from  ra_file_details, ros_setup_details where created_date >= :startDate " +
//            "and created_date < :endDate and ros_setup_details.market = :market and ra_file_details.ra_prov_details_id = ros_setup_details.id" +
//            " order by ra_prov_details_id limit :limit offset :offset", nativeQuery = true)
//    List<Long> findRosterSetUpIdsBetweenDatesForMarket(@Param("market") String market, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
//                                                       @Param("limit") int limit, @Param("offset") int offset);
//
//    @Query(value = "select distinct(ra_prov_details_id) from ra_file_details, ros_setup_details where created_date >= :startDate " +
//            "and created_date < :endDate and ros_setup_details.state = :state and ra_file_details.ra_prov_details_id = ros_setup_details.id" +
//            " order by ra_prov_details_id limit :limit offset :offset", nativeQuery = true)
//    List<Long> findRosterSetUpIdsBetweenDatesForState(@Param("state") String state, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
//                                                       @Param("limit") int limit, @Param("offset") int offset);
//
//    @Query(value = "select * from ra_file_details where id = :rosterFileId", nativeQuery = true)
//    RAFileDetails findRosterFileById(Long rosterFileId);

    @Query(value = "select * from ra_file_details where ra_prov_details_id in (:raProvDetailsIds) " +
            "and creat_dt >= :startDate and creat_dt < :endDate order by creat_dt desc offset :offset rows fetch next :limit rows only", nativeQuery = true)
    List<RAFileDetails> findRAFileDetailsListBetweenDatesFromRAProvDetailsIds(Date startDate, Date endDate, List<Long> raProvDetailsIds, @Param("limit") int limit, @Param("offset") int offset);
}

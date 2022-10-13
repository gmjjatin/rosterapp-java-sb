//package com.hilabs.rostertracker.repository;
//import java.util.List;
//
//import com.hilabs.roster.entity.RosterUser;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserRepository extends CrudRepository<RosterUser, String>{
//    RosterUser findByUserId(String userId);
//
//    @Query(value = "SELECT * FROM roster_user where USER_ID=:userId AND ACTIVE_FLAG=1",
//            nativeQuery = true)
//    RosterUser findActiveUserByUserId(@Param("userId") String userId);
//
//    @Modifying
//    @Query(value = "UPDATE roster_user SET PWD = :encodedPassword "
//            + "WHERE USER_ID = :userId", nativeQuery = true)
//    void updatePassword(@Param("userId") String userId, @Param("encodedPassword") String encodedPassword);
//
//    @Query(value = "SELECT du.USER_ID, du.FIRST_NAME ,du.LAST_NAME ,duur.ROLE_CD FROM roster_user du JOIN roster_user_x_user_role duur  "
//            + "ON du.USER_ID = duur.USER_ID WHERE du.ACTIVE_FLAG = 1 ORDER BY du.USER_ID ASC", nativeQuery = true)
//    List<Object[]> getUsersWithRoles();
//}

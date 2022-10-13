//package com.hilabs.rostertracker.repository;
//
//import com.hilabs.roster.entity.RosterUser;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface RosterRoleRepository extends CrudRepository<RosterUser, String>{
//    @Query(value = "SELECT DISTINCT duur.ROLE_CD FROM ROSTER_ROLE duur"
//            , nativeQuery = true)
//    List<String> getRoles();
//
//
//}
package com.anthem.rostertracker.repository;

import com.anthem.rostertracker.entity.RosterUserXRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface RosterUserXRoleRepository extends CrudRepository<RosterUserXRole, String>{

    @Query(value = "SELECT duur.ROLE_CD FROM roster_user_x_user_role duur "
            + "WHERE duur.USER_ID = :userId", nativeQuery = true)
    String getRole(@Param("userId") String userId);

    @Modifying
    @Query(value = "UPDATE roster_user_x_user_role duur SET duur.ROLE_CD = :roleCd, duur.LAST_UPDT_DT = :date, duur.LAST_UPDT_USER_ID = :createdORUpdatedUserId "
            + "WHERE duur.USER_ID = :userId", nativeQuery = true)
    void updateRoleCd(@Param("userId") String userId,@Param("roleCd") String roleCd,@Param("date") Date date, @Param("createdORUpdatedUserId") String createdORUpdatedUserId);

}

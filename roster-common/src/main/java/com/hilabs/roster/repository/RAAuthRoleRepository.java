package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAAuthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RAAuthRoleRepository  extends JpaRepository<RAAuthRole, Long> {
    @Query(value = "select * from RA_AUTH_ROLE raf" +
            " where ID IN (SELECT rarf.ROLE_ID FROM " +
            "                                           RA_AUTH_GROUP_X_ROLE ragr, RA_AUTH_GROUP rag" +
            "                               and ragr.GROUP_ID = rag.ID" +
            "                               and rag.GROUP_NAME IN (:groupNames))", nativeQuery = true)
    List<RAAuthRole> findRolesForGroups(List<String> groupNames);
}

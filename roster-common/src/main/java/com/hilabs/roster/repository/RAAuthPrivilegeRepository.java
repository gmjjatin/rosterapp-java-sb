package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAAuthPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RAAuthPrivilegeRepository extends JpaRepository<RAAuthPrivilege, Long> {
    @Query(value = "select * from RA_AUTH_PRIVILEGE raf" +
            " where ID IN (SELECT rarf.PRIVILEGE_ID FROM " +
            "                                           RA_AUTH_ROLE_X_PRIVILEGE rarf, " +
            "                                           RA_AUTH_GROUP_X_ROLE ragr, RA_AUTH_GROUP rag" +
            "                               where rarf.ROLE_ID = ragr.ROLE_ID" +
            "                               and ragr.GROUP_ID = rag.ID" +
            "                               and ragr.IS_ACTIVE = 1 " +
            "                               and rag.IS_ACTIVE = 1 " +
            "                               and rarf.IS_ACTIVE = 1 " +
            "                               and rag.GROUP_NAME IN (:groupNames))" +
            " and raf.IS_ACTIVE = 1", nativeQuery = true)
    List<RAAuthPrivilege> findGroupPrivileges(List<String> groupNames);


}

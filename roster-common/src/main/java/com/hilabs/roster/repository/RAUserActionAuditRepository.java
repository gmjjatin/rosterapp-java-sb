package com.hilabs.roster.repository;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAUserActionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RAUserActionAuditRepository extends JpaRepository<RAUserActionAudit, Long> {
    @Query(value = "select * from RA_RT_USER_ACTN_AUDIT where ACTN_OBJCT_ID = :actionObjectId and ACTN_OBJCT_TYPE = :actionObjectType " +
            "and USER_ACTN = :userAction", nativeQuery = true)
    List<RAUserActionAudit> findRAUserActionAuditList(String actionObjectId, String actionObjectType, String userAction);
}

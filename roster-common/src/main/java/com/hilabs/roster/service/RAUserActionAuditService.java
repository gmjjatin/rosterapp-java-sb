package com.hilabs.roster.service;

import com.hilabs.roster.entity.RAUserActionAudit;
import com.hilabs.roster.repository.RAUserActionAuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RAUserActionAuditService {
    @Autowired
    private RAUserActionAuditRepository raUserActionAuditRepository;

    public RAUserActionAudit saveRAUserActionAudit(final String actionObjectId, final String actionObjectType, final String userAction, final Date createdDate, final String createdUserId) {
        RAUserActionAudit raUserActionAudit = new RAUserActionAudit(actionObjectId, actionObjectType, userAction, createdDate, createdUserId);
        return raUserActionAuditRepository.save(raUserActionAudit);
    }

    public List<RAUserActionAudit> findRAUserActionAuditList(String actionObjectId, String actionObjectType, String userAction) {
        return raUserActionAuditRepository.findRAUserActionAuditList(actionObjectId, actionObjectType, userAction);
    }
}

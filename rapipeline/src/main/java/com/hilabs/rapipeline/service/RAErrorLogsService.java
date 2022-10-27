package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.repository.RAFileErrorCodeDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RAErrorLogsService {
    @Autowired
    private RAFileErrorCodeDetailRepository raFileErrorCodeDetailRepository;
    public void insertRASystemErrors(Long raFileDetailsId, String errorCode, String errorDescription, Integer statusCode) {
        try {
            RAFileErrorCodeDetails raErrorLogs = new RAFileErrorCodeDetails(raFileDetailsId, errorCode, errorDescription, statusCode);
            raFileErrorCodeDetailRepository.save(raErrorLogs);
        } catch (Exception ex) {
            log.error("Error in insertRASystemErrors - ex {}", ex.getMessage());
            throw ex;
        }
    }
}

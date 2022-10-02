package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RAErrorLogs;
import com.hilabs.roster.repository.RAErrorLogsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RAErrorLogsService {
    @Autowired
    private RAErrorLogsRepository raErrorLogsRepository;
    public void insertRASystemErrors(Long raFileDetailsId, Long raSheetDetailsId, String stageName, Long errorCodeDetailsId, String errorDescription,
                                     String errorLongDescription) {
        try {
            RAErrorLogs raErrorLogs = new RAErrorLogs(raFileDetailsId, raSheetDetailsId, stageName, errorCodeDetailsId,
                    errorDescription, errorLongDescription);
            raErrorLogsRepository.save(raErrorLogs);
        } catch (Exception ex) {
            log.error("Error in insertRASystemErrors - ex {}", ex.getMessage());
            throw ex;
        }
    }
}

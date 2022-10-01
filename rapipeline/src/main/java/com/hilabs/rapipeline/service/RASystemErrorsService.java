package com.hilabs.rapipeline.service;

import com.hilabs.roster.repository.RASystemErrorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RASystemErrorsService {
    @Autowired
    private RASystemErrorsRepository raSystemErrorsRepository;

    public void insertRASystemErrors(Long raFileDetailsId, String lastStage, Integer lastStatus, String errorCategory, String errorDescription, String errorStackTrace, Long createdUserId, Long lastUpdateUserId) {
        raSystemErrorsRepository.insertRASystemErrors(raFileDetailsId, lastStage, lastStatus,
                 errorCategory, errorDescription, errorStackTrace, createdUserId, lastUpdateUserId);
    }
}

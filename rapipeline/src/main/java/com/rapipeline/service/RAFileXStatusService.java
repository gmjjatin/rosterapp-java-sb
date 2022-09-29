package com.rapipeline.service;

import com.rapipeline.entity.RAFileXStatus;
import com.rapipeline.repository.RAFileXStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RAFileXStatusService {
    @Autowired
    RAFileXStatusRepository raFileXStatusRepository;

    public void insertOrUpdateRAFileXStatus(Long raFileDetailsId, int statusCode) {
        Optional<RAFileXStatus> optionalRAFileXStatus = raFileXStatusRepository.findByRAFileDetailsId(raFileDetailsId);
        //TODO need to confirm if we need update status always
        if (optionalRAFileXStatus.isPresent()) {
            raFileXStatusRepository.updateRAFileXStatus(raFileDetailsId, statusCode);
        } else {
            raFileXStatusRepository.insertRAFileXStatus(raFileDetailsId, statusCode);
        }
    }
}

package com.rapipeline.service;

import com.rapipeline.entity.RAFileDetails;
import com.rapipeline.repository.RAFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RAFileDetailsService {
    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;

    public void insertRAFileDetails(Long raProvDetailsId,String originalFileName, String standardizedFileName,String plmTicketId, String fileLocation,String fileSystem, Long createdUserId,Long lastUpdateUserId) {
        raFileDetailsRepository.insertRAFileDetails(raProvDetailsId, originalFileName, standardizedFileName, plmTicketId, fileLocation, fileSystem, createdUserId, lastUpdateUserId);
    }

    public Optional<RAFileDetails> findByFileName(String originalFileName) {
        return raFileDetailsRepository.findByFileName(originalFileName);
    }
}

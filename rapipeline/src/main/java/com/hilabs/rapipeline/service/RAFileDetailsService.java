package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class RAFileDetailsService {
    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;

    public Long insertRAFileDetails(Long raProvDetailsId, String originalFileName, String standardizedFileName,
                                    String market, int statusCode, String createdUserId, String lastUpdateUserId) {
        RAFileDetails raFileDetails = new RAFileDetails();
        raFileDetails.setRaProvDetailsId(raProvDetailsId);
        raFileDetails.setOriginalFileName(originalFileName);
        raFileDetails.setStandardizedFileName(standardizedFileName);
        raFileDetails.setMarket(market);
        raFileDetails.setStatusCode(statusCode);
        raFileDetails.setIsActive(1);
        raFileDetails.setCreatedUserId(createdUserId);
        raFileDetails.setLastUpdatedUserId(lastUpdateUserId);
        raFileDetails.setCreatedDate(new Date());
        raFileDetails.setLastUpdatedDate(new Date());
        raFileDetails = raFileDetailsRepository.save(raFileDetails);
        return raFileDetails.getId();
    }

    public Optional<RAFileDetails> findByRAFileDetailsId(Long raFileDetailsId) {
        return raFileDetailsRepository.findByRAFileDetailsId(raFileDetailsId);
    }
}

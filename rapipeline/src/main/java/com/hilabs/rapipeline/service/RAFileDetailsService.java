package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RAFileDetailsService {
    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;

    public void insertRAFileDetails(Long raProvDetailsId, String market, String lob, String originalFileName, String standardizedFileName,String plmTicketId, String fileLocation,String fileSystem, Long createdUserId,Long lastUpdateUserId) {
        raFileDetailsRepository.insertRAFileDetails(raProvDetailsId, market, lob, originalFileName, standardizedFileName, plmTicketId, fileLocation, fileSystem, createdUserId, lastUpdateUserId);
    }

    public void updateRAFileDetails(RAFileDetails raFileDetails, Long raProvDetailsId, String market, String lob, String originalFileName, String standardizedFileName, String plmTicketId, String fileLocation, String fileSystem, Long lastUpdateUserId) {
        raProvDetailsId = raProvDetailsId == null ? raFileDetails.getRaProvDetailsId() : raProvDetailsId;
        market = market == null ? raFileDetails.getMarket() : market;
        lob = lob == null ? raFileDetails.getLineOfBusiness() : lob;
        originalFileName = originalFileName == null ? raFileDetails.getOriginalFileName() : originalFileName;
        standardizedFileName = standardizedFileName == null ? raFileDetails.getStandardizedFileName() : standardizedFileName;
        plmTicketId = plmTicketId == null ? raFileDetails.getPlmTicketId() : plmTicketId;
        fileLocation = fileLocation == null ? raFileDetails.getFileLocation() : fileLocation;
        fileSystem = fileSystem == null ? raFileDetails.getFileSystem() : fileSystem;
        raFileDetailsRepository.updateRAFileDetails(raProvDetailsId, market, lob, originalFileName, standardizedFileName, plmTicketId, fileLocation, fileSystem, lastUpdateUserId);
    }

    public Optional<RAFileDetails> findByFileName(String originalFileName) {
        return raFileDetailsRepository.findByFileName(originalFileName);
    }

    public Optional<RAFileDetails> findByRAFileDetailsId(Long raFileDetailsId) {
        return raFileDetailsRepository.findByRAFileDetailsId(raFileDetailsId);
    }
}

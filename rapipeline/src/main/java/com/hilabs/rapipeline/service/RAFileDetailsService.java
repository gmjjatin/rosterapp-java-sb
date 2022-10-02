package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
        raFileDetails = raFileDetailsRepository.save(raFileDetails);
        return raFileDetails.getId();
    }

//    public void updateRAFileDetails(RAFileDetails raFileDetails, Long raProvDetailsId, String market, String lob, String originalFileName, String standardizedFileName, String plmTicketId, String fileLocation, String fileSystem, Long lastUpdateUserId) {
//        raProvDetailsId = raProvDetailsId == null ? raFileDetails.getRaProvDetailsId() : raProvDetailsId;
//        market = market == null ? raFileDetails.getMarket() : market;
//        lob = lob == null ? raFileDetails.getLineOfBusiness() : lob;
//        originalFileName = originalFileName == null ? raFileDetails.getOriginalFileName() : originalFileName;
//        standardizedFileName = standardizedFileName == null ? raFileDetails.getStandardizedFileName() : standardizedFileName;
//        plmTicketId = plmTicketId == null ? raFileDetails.getPlmTicketId() : plmTicketId;
//        fileLocation = fileLocation == null ? raFileDetails.getFileLocation() : fileLocation;
//        fileSystem = fileSystem == null ? raFileDetails.getFileSystem() : fileSystem;
//        raFileDetailsRepository.updateRAFileDetails(raProvDetailsId, market, lob, originalFileName, standardizedFileName, plmTicketId, fileLocation, fileSystem, lastUpdateUserId);
//    }

//    public Optional<RAFileDetails> findByFileName(String originalFileName) {
//        return raFileDetailsRepository.findByFileName(originalFileName);
//    }

    public Optional<RAFileDetails> findByRAFileDetailsId(Long raFileDetailsId) {
        return raFileDetailsRepository.findByRAFileDetailsId(raFileDetailsId);
    }
}

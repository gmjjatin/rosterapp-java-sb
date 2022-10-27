package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RAFileDetailsService {
    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;

    public static String getStrOrNA(String sourceName) {
        return sourceName == null ? "N/A" : sourceName;
    }

    public Long insertRAFileDetails(String sourceName, String originalFileName, String standardizedFileName,
                                    String market, int statusCode) {
        RAFileDetails raFileDetails = new RAFileDetails();
        raFileDetails.setSourceName(getStrOrNA(sourceName));
        raFileDetails.setOriginalFileName(getStrOrNA(originalFileName));
        raFileDetails.setStandardizedFileName(getStrOrNA(standardizedFileName));
        raFileDetails.setMarket(getStrOrNA(market));
        raFileDetails.setStatusCode(statusCode);
        raFileDetails.setIsActive(1);
        raFileDetails.setCreatedUserId("SYSTEM");
        raFileDetails.setLastUpdatedUserId("SYSTEM");
        raFileDetails.setCreatedDate(new Date());
        raFileDetails.setLastUpdatedDate(new Date());
        raFileDetails.setManualActionRequired(0);
        raFileDetails = raFileDetailsRepository.save(raFileDetails);
        return raFileDetails.getId();
    }

    public Optional<RAFileDetails> findByRAFileDetailsId(Long raFileDetailsId) {
        return raFileDetailsRepository.findByRAFileDetailsId(raFileDetailsId);
    }

    public List<RAFileDetails> findFileDetailsByStatusCodesWithManualActionReqList(List<Integer> statusCodes, List<Integer> manualActionRequiredList ,int limit, int offset) {
        return raFileDetailsRepository.findFileDetailsByStatusCodesWithManualActionReqList(statusCodes, manualActionRequiredList, limit, offset);
    }

    public void updateRAFileDetailsStatus(Long raFileDetailsId, Integer status) {
        raFileDetailsRepository.updateRAFileDetailsStatus(raFileDetailsId, status);
    }
}

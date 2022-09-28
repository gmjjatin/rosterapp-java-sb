package com.rapipeline.service;

import com.google.gson.Gson;
import com.rapipeline.entity.RAFileMetaDataDetails;
import com.rapipeline.repository.RAFileMetaDataDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RAFileMetaDataDetailsService {
    private static final Gson gson = new Gson();
    @Autowired
    private RAFileMetaDataDetailsRepository raFileMetaDataDetailsRepository;

    public List<RAFileMetaDataDetails> getUnIngestedRAFileMetaDataDetails(int maxRetryNo) {
        return raFileMetaDataDetailsRepository.getUnIngestedRAFileMetaDataDetails(maxRetryNo);
    }

    //TODO later - need to add more checks
    public List<String> validateMetaDataAndGetErrorList(RAFileMetaDataDetails raFileMetaDataDetails) {
        List<String> missingFields = new ArrayList<>();
        if (raFileMetaDataDetails.getProviderName() == null) {
            missingFields.add("Provider Name");
        }
        if (raFileMetaDataDetails.getMarket() == null) {
            missingFields.add("market");
        }
        if (raFileMetaDataDetails.getLineOfBusiness() == null) {
            missingFields.add("Line Of Business");
        }
        if (raFileMetaDataDetails.getPlmTicketId() == null) {
            missingFields.add("PLM Ticket Id");
        }
        if (raFileMetaDataDetails.getFileName() == null) {
            missingFields.add("File Name");
        }
        List<String> errorList = new ArrayList<>();
        if (missingFields.size() > 0) {
            errorList.add("Missing fields - " + String.join(", ", missingFields));
        }
        if (!raFileMetaDataDetails.getFileName().endsWith(".xlsx")) {
            errorList.add("File name doesn't end with .xlsx");
        }
        return errorList;
    }

    public boolean updateStatusForRAFileMetaDataDetails(RAFileMetaDataDetails raFileMetaDataDetails, int ingestionStatus) {
        try {
            raFileMetaDataDetailsRepository.updateStatusForRAFileMetaDataDetails(raFileMetaDataDetails.getId(), ingestionStatus);
            return true;
        } catch (Exception ex) {
            log.error("Error in updateRAFileMetaDataDetails - raFileMetaDataDetails {} ingestionStatus {} ex {}", gson.toJson(raFileMetaDataDetails),
                    ingestionStatus, ex.getMessage());
            return false;
        }
    }

    public boolean incrementRetryNoForRAFileMetaDataDetails(RAFileMetaDataDetails raFileMetaDataDetails) {
        try {
            raFileMetaDataDetailsRepository.incrementRetryNoForRAFileMetaDataDetails(raFileMetaDataDetails.getId());
            return true;
        } catch (Exception ex) {
            log.error("Error in incrementRetryNoForRAFileMetaDataDetails - raFileMetaDataDetails {} ex {}", gson.toJson(raFileMetaDataDetails),
                    ex.getMessage());
            return false;
        }
    }
}

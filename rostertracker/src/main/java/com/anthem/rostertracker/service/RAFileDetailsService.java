package com.anthem.rostertracker.service;

import com.anthem.rostertracker.entity.RAFileDetails;
import com.anthem.rostertracker.entity.RAProvDetails;
import com.anthem.rostertracker.entity.RASheetDetails;
import com.anthem.rostertracker.model.RAFileDetailsListAndSheetList;
import com.anthem.rostertracker.repository.RAFileDetailsRepository;
import com.anthem.rostertracker.repository.RASheetDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RAFileDetailsService {
    @Autowired
    RAProviderService raProviderService;

    @Autowired
    RAFileDetailsRepository raFileDetailsRepository;

    @Autowired
    RASheetDetailsRepository raSheetDetailsRepository;

    public RAFileDetailsListAndSheetList getRosterSourceListAndFilesList(Long raFileDetailsId, Integer providerId, String market, String lineOfBusiness,
                                                                         long startTime, long endTime, int limit, int offset) {
        List<RAFileDetails> raFileDetailsList = getRAFileDetailsList(raFileDetailsId, providerId, market, lineOfBusiness, startTime, endTime, limit, offset);
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream().map(RAFileDetails::getId).collect(Collectors.toList()));
        return new RAFileDetailsListAndSheetList(raFileDetailsList, raSheetDetailsList);
    }

    public List<RAFileDetails> getRAFileDetailsList(Long raFileDetailsId, Integer providerId, String market, String lineOfBusiness, long startTime, long endTime,
                                                   int limit, int offset) {
        if (raFileDetailsId != null && raFileDetailsId > 0) {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(raFileDetailsId);
            if (optionalRAFileDetails.isPresent()) {
                return Collections.singletonList(optionalRAFileDetails.get());
            }
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        List<RAProvDetails> raProvDetailsList = getRAProvDetailsList(providerId, market, lineOfBusiness);
        return raFileDetailsRepository.findRAFileDetailsListBetweenDatesFromRAProvDetailsIds(startDate, endDate,
                raProvDetailsList.stream().map(RAProvDetails::getId).collect(Collectors.toList()), limit, offset);
    }

    public List<RAProvDetails> getTopRAProvDetailsList() {
        //TODO manikanta
        return raProviderService.getAllProviders();
    }

    public List<RAProvDetails> getRAProvDetailsList(Integer providerId, String market, String lineOfBusiness) {
        if (providerId != null && providerId > 0) {
            Optional<RAProvDetails> optionalRAProvDetails = raProviderService.getRAProvDetailsFromProviderId(providerId);
            return optionalRAProvDetails.map(Collections::singletonList).orElseGet(ArrayList::new);
        } else if ((market != null && !market.isEmpty()) && (lineOfBusiness != null && !lineOfBusiness.isEmpty())) {
            return raProviderService.getRosterSourceListFromMarketAndState(market, lineOfBusiness);
        } else if (market != null && !market.isEmpty()) {
            return raProviderService.getRosterSourceListFromMarket(market);
        } else if (lineOfBusiness != null && !lineOfBusiness.isEmpty()) {
            return raProviderService.getRosterSourceListFromLineOfBusiness(lineOfBusiness);
        } else {
            return getTopRAProvDetailsList();
        }
    }

    public Optional<RAFileDetails> findRAFileDetailsById(Long rosterFileId) {
        return raFileDetailsRepository.findById(rosterFileId);
    }
}

package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAProvDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.rostertracker.model.RAFileDetailsListAndSheetList;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hilabs.rostertracker.service.RAProviderService.getAdjustedString;

@Service
@Log4j2
public class RAFileDetailsService {
    @Autowired
    RAProviderService raProviderService;

    @Autowired
    RAFileDetailsRepository raFileDetailsRepository;

    @Autowired
    RASheetDetailsRepository raSheetDetailsRepository;

    public RAFileDetailsListAndSheetList getRosterSourceListAndFilesList(Long raFileDetailsId, String market, String lineOfBusiness,
                                                                         long startTime, long endTime, int limit, int offset, List<Integer> statusCodes) {
        List<RAFileDetails> raFileDetailsList = getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness, startTime, endTime, limit, offset, statusCodes);
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream().map(RAFileDetails::getId).collect(Collectors.toList()));
        return new RAFileDetailsListAndSheetList(raFileDetailsList, raSheetDetailsList);
    }

    public List<RAFileDetails> getRAFileDetailsList(Long raFileDetailsId, String market, String lineOfBusiness, long startTime, long endTime,
                                                   int limit, int offset, List<Integer> statusCodes) {
        if (raFileDetailsId != null && raFileDetailsId > 0) {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(raFileDetailsId);
            if (optionalRAFileDetails.isPresent()) {
                return Collections.singletonList(optionalRAFileDetails.get());
            }
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        //TODO demo fix limit and offset
        List<RAFileDetails> raFileDetailsList = getRAFileDetailsList(market, lineOfBusiness, startDate, endDate, limit, offset);
        raFileDetailsList = raFileDetailsList.stream().filter(p -> {
            Date date = p.getCreatedDate();
            //TODO
            if (date == null) {
                return true;
            }
            return startDate.getTime() <= date.getTime() && endDate.getTime() > date.getTime();
        }).collect(Collectors.toList());
//        List<RAFileDetails> raFileDetailsList = raFileDetailsRepository.findRAFileDetailsListBetweenDatesFromRAProvDetailsIds(startDate, endDate,
//                raProvDetailsList.stream().map(RAProvDetails::getId).collect(Collectors.toList()), limit, offset);
        return raFileDetailsList.stream().filter(p -> p.getStatusCode() != null && statusCodes.stream().anyMatch(ss -> p.getStatusCode() == ss)).collect(Collectors.toList());
    }

    public List<RAProvDetails> getTopRAProvDetailsList() {
        //TODO manikanta
        return raProviderService.getAllProviders();
    }

    public List<RAFileDetails> getRAFileDetailsList(String market, String lineOfBusiness, Date startDate, Date endDate, int limit, int offset) {
        //TODO handle limit and offset
        if ((market != null && !market.isEmpty()) && (lineOfBusiness != null && !lineOfBusiness.isEmpty())) {
            return raProviderService.getRosterSourceListFromMarketAndState(market, lineOfBusiness);
        } else if (market != null && !market.isEmpty()) {
            return raProviderService.getRosterSourceListFromMarket(market);
        } else if (lineOfBusiness != null && !lineOfBusiness.isEmpty()) {
            return raProviderService.getRosterSourceListFromLineOfBusiness(lineOfBusiness);
        } else {
//            return getTopRAProvDetailsList();
            return raFileDetailsRepository.findRAFileDetailsListBetweenDates(startDate, endDate, limit, offset);
        }
    }

    public Optional<RAFileDetails> findRAFileDetailsById(Long rosterFileId) {
        return raFileDetailsRepository.findById(rosterFileId);
    }


    public ConcurrentLruCache<String, List<RAFileDetails>> fileSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByFileSearchStr(p);
    });
    public List<RAFileDetails> findByProviderSearchStr(String providerSearchStr) {
        try {
            if (fileSearchStrCache.contains(providerSearchStr)) {
                return fileSearchStrCache.get(providerSearchStr);
            }
            String adjustedKey = getAdjustedString(providerSearchStr, 3);
            //TODO
            populateFileSearchStrCache(providerSearchStr);
            return fileSearchStrCache.get(adjustedKey);
        } catch (Exception ex) {
            return raFileDetailsRepository.findByFileSearchStr(providerSearchStr);
        }
    }

    //TODO
    @Async
    public void populateFileSearchStrCache(String searchStr) {
        try {
            fileSearchStrCache.get(searchStr);
        } catch (Exception ex) {
            log.error("Error in populateProviderSearchStrCache searchStr {} ex {}", searchStr,
                    ex.getMessage());
        }
    }
}

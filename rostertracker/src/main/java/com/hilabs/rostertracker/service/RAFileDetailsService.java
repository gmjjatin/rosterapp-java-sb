package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsLobRepository;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.rostertracker.model.RAFileDetailsListAndSheetList;
import com.hilabs.rostertracker.utils.RosterUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import java.util.*;
import java.util.stream.Collectors;

import static com.hilabs.roster.util.FileUtils.getAdjustedString;

@Service
@Log4j2
public class RAFileDetailsService {

    @Autowired
    RAFileDetailsRepository raFileDetailsRepository;
    public ConcurrentLruCache<String, List<RAFileDetails>> fileSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByFileSearchStr(p);
    });
    public ConcurrentLruCache<String, List<RAFileDetails>> marketSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByMarketSearchStr(p);
    });
    public ConcurrentLruCache<String, List<RAFileDetails>> lineOfBusinessSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByLineOfBusinessSearchStr(p);
    });

    @Autowired
    RASheetDetailsRepository raSheetDetailsRepository;
    @Autowired
    RAFileDetailsLobRepository raFileDetailsLobRepository;
    List<String> allMarkets = null;
    private List<String> allLineOfBusiness = null;

    public RAFileDetailsListAndSheetList getRosterSourceListAndFilesList(Long raFileDetailsId, String market, String lineOfBusiness,
                                                                         long startTime, long endTime, int limit, int offset, List<Integer> statusCodes) {
        List<RAFileDetails> raFileDetailsList = getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness, startTime, endTime, statusCodes, limit, offset);
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream().map(RAFileDetails::getId).collect(Collectors.toList()));
        return new RAFileDetailsListAndSheetList(raFileDetailsList, raSheetDetailsList);
    }

    public List<RAFileDetails> getRAFileDetailsList(Long raFileDetailsId, String market, String lineOfBusiness, long startTime, long endTime,
                                                    List<Integer> statusCodes, int limit, int offset) {
        if (raFileDetailsId != null && raFileDetailsId > 0) {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(raFileDetailsId);
            if (optionalRAFileDetails.isPresent()) {
                return Collections.singletonList(optionalRAFileDetails.get());
            }
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        return getRAFileDetailsList(market, lineOfBusiness, startDate, endDate, statusCodes, limit, offset);
//        List<RAFileDetails> resRaFileDetailsList = new ArrayList<>();
//        for (RAFileDetails raFileDetails : raFileDetailsList) {
//            if (raFileDetails.getStatusCode() != null && statusCodes.stream().anyMatch(ss -> raFileDetails.getStatusCode().equals(ss))) {
//                resRaFileDetailsList.add(raFileDetails);
//            }
//        }
//        return resRaFileDetailsList;
    }

    public List<RAFileDetails> getRAFileDetailsList(String market, String lineOfBusiness, Date startDate, Date endDate,
                                                    List<Integer> statusCodes, int limit, int offset) {
        //TODO handle limit and offset
        if ((market != null && !market.isEmpty()) && (lineOfBusiness != null && !lineOfBusiness.isEmpty())) {
            return getRosterSourceListFromMarketAndState(market, lineOfBusiness, startDate, endDate, statusCodes,
                    limit, offset);
        } else if (market != null && !market.isEmpty()) {
            return getRosterSourceListFromMarket(market, startDate, endDate, statusCodes, limit, offset);
        } else if (lineOfBusiness != null && !lineOfBusiness.isEmpty()) {
            return getRosterSourceListFromLineOfBusiness(lineOfBusiness, startDate, endDate, statusCodes, limit, offset);
        } else {
            return raFileDetailsRepository.findRAFileDetailsListBetweenDates(startDate, endDate, statusCodes, limit, offset);
        }
    }

    public Optional<RAFileDetails> findRAFileDetailsById(Long rosterFileId) {
        return raFileDetailsRepository.findById(rosterFileId);
    }

    public List<RAFileDetails> findByFileSearchStr(String providerSearchStr) {
        try {
            if (fileSearchStrCache.contains(providerSearchStr)) {
                return fileSearchStrCache.get(providerSearchStr);
            }
            String adjustedKey = getAdjustedString(providerSearchStr, 10);
            //TODO
            populateFileSearchStrCache(providerSearchStr);
            return fileSearchStrCache.get(adjustedKey);
        } catch (Exception ex) {
            return raFileDetailsRepository.findByFileSearchStr(providerSearchStr);
        }
    }

    public List<RASheetDetails> getRAFileDetailsList(Long raFileDetailsId) {
        log.debug("Fetch sheet data for raFileDetailsId : {}", raFileDetailsId);
        return raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
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

    public List<RAFileDetails> findByMarketSearchStr(String searchStr) {
        try {
            if (marketSearchStrCache.contains(searchStr)) {
                return marketSearchStrCache.get(searchStr);
            }
            String adjustedKey = getAdjustedString(searchStr, 3);
            populateMarketSearchStrCache(searchStr);
            return marketSearchStrCache.get(adjustedKey);
        } catch (Exception ex) {
            return raFileDetailsRepository.findByMarketSearchStr(searchStr);
        }
    }

    @Async
    public void populateMarketSearchStrCache(String marketSearchStr) {
        try {
            marketSearchStrCache.get(marketSearchStr);
        } catch (Exception ex) {
            log.error("Error in populateMarketSearchStrCache marketSearchStr {} ex {}", marketSearchStr, ex.getMessage());
        }
    }

    public List<RAFileDetails> findByLineOfBusinessSearchStr(String searchStr) {
        try {
            if (lineOfBusinessSearchStrCache.contains(searchStr)) {
                return lineOfBusinessSearchStrCache.get(searchStr);
            }
            String adjustedKey = getAdjustedString(searchStr, 3);
            populateLineOfBusinessSearchStrCache(searchStr);
            return lineOfBusinessSearchStrCache.get(adjustedKey);
        } catch (Exception ex) {
            return raFileDetailsRepository.findByLineOfBusinessSearchStr(searchStr);
        }
    }

    public List<RAFileDetails> getRosterSourceListFromMarketAndState(String market, String lineOfBusiness, Date startDate,
                                                                     Date endDate, List<Integer> statusCodes,
                                                                     int limit, int offset) {
        return raFileDetailsRepository.findByMarketAndLineOfBusiness(market, lineOfBusiness, startDate, endDate, statusCodes, limit, offset);
    }

    public List<RAFileDetails> getRosterSourceListFromMarket(String market, Date startDate, Date endDate,
                                                             List<Integer> statusCodes, int limit, int offset) {
        return raFileDetailsRepository.findByMarket(market, startDate, endDate, statusCodes, limit, offset);
    }

    public List<RAFileDetails> getRosterSourceListFromLineOfBusiness(String lineOfBusiness, Date startDate, Date endDate,
                                                                     List<Integer> statusCodes, int limit, int offset) {
        return raFileDetailsRepository.findByLineOfBusiness(lineOfBusiness, startDate, endDate, statusCodes, limit, offset);
    }

    @Async
    public void populateLineOfBusinessSearchStrCache(String lineOfBusinessSearchStr) {
        try {
            raFileDetailsRepository.findByLineOfBusinessSearchStr(lineOfBusinessSearchStr);
        } catch (Exception ex) {
            log.error("Error in populateLineOfBusinessSearchStrCache lineOfBusinessSearchStr {} ex {}", lineOfBusinessSearchStr, ex.getMessage());
        }
    }

    public List<String> findAllMarkets(List<Integer> statusCodes) {
        if (allMarkets == null) {
            allMarkets = raFileDetailsRepository.findAllMarkets(statusCodes);
        }
        return allMarkets;
    }

    public List<String> findAllLineOfBusiness() {
        if (allLineOfBusiness == null) {
            allLineOfBusiness = raFileDetailsLobRepository.findAllLineOfBusinesses();
        }
        return allLineOfBusiness;
    }

    public List<RAFileDetails> getRAFileDetailsListFromSearchStr(String searchStr) {
        if (searchStr == null || searchStr.isEmpty()) {
//            return findTopEntriesOrderBySourceName(DEFAULT_NO_OF_ENTRIES);
            return new ArrayList<>();
        }
//        List<RAFileDetails> raProvDetailsDetailsByProvider = findByProviderSearchStr(searchStr);
        List<RAFileDetails> raProvDetailsDetailsByMarket = findByMarketSearchStr(searchStr);
        List<RAFileDetails> raProvDetailsDetailsByLineOfBusiness = findByLineOfBusinessSearchStr(searchStr);
        List<RAFileDetails> allRAProvDetailsList = new ArrayList<>();

        allRAProvDetailsList.addAll(raProvDetailsDetailsByMarket);
        allRAProvDetailsList.addAll(raProvDetailsDetailsByLineOfBusiness);
        return RosterUtils.removeDuplicateRAProvList(allRAProvDetailsList);
    }
}

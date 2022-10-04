package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAProvDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RAProvDetailsRepository;
import com.hilabs.roster.repository.RAProvMarketLobMapRepository;
import com.hilabs.rostertracker.utils.RosterUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class RAProviderService {

    public static final int DEFAULT_NO_OF_ENTRIES = 5;
    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;

    @Autowired
    private RAProvDetailsRepository raProvDetailsRepository;

    @Autowired
    private RAProvMarketLobMapRepository raProvMarketLobMapRepository;

    public List<RAProvDetails> getAllProviders() {
        return raProvDetailsRepository.getAllProviders();
    }

    public Optional<RAProvDetails> getRAProvDetailsFromProviderId(Integer providerId) {
        RAProvDetails raProvDetails = raProvDetailsRepository.findByProviderById(providerId);
        if (raProvDetails == null) {
            return Optional.empty();
        }
        return Optional.of(raProvDetails);
    }

//    public List<RAProvDetails> getRosterSourceListFromLineOfBusiness(String lineOfBusiness, Date startDate, Date endDate) {
//        List<Long> rosterSourceIds = raFileDetailsRepository.findRosterSetUpIdsBetweenDatesForState(state, startDate, endDate);
//        return raProvDetailsRepository.findRAProvDetailsFromIds(rosterSourceIds);
//    }
//
//    public List<RAProvDetails> getRosterSourceList(Date startDate, Date endDate) {
//        List<Long> rosterSourceIds = raFileDetailsRepository.findRAProvDetailsIdsBetweenDates(startDate, endDate);
//        return raProvDetailsRepository.findRAProvDetailsFromIds(rosterSourceIds);
//    }

    static List<RAProvDetails> topRAProvDetailsName = null;
    public List<RAProvDetails> findTopEntriesOrderBySourceName(int noOfEntries) {
        if (topRAProvDetailsName == null) {
            topRAProvDetailsName = raProvDetailsRepository.findTopEntriesOrderBySourceName(noOfEntries);
        }
        return topRAProvDetailsName;
    }


    public ConcurrentLruCache<String, List<RAProvDetails>> providerSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raProvDetailsRepository.findByProviderSearchStr(p);
    });

    public static String getAdjustedString(String str, int maxLength) {
        String adjustedKey = str;
        if (str.length() > maxLength) {
            adjustedKey = str.substring(0, maxLength);
        }
        return adjustedKey;
    }
    public List<RAProvDetails> findByProviderSearchStr(String providerSearchStr) {
        try {
            if (providerSearchStrCache.contains(providerSearchStr)) {
                return providerSearchStrCache.get(providerSearchStr);
            }
            String adjustedKey = getAdjustedString(providerSearchStr, 3);
            populateProviderSearchStrCache(providerSearchStr);
            return providerSearchStrCache.get(adjustedKey);
        } catch (Exception ex) {
            return raProvDetailsRepository.findByProviderSearchStr(providerSearchStr);
        }
    }

    @Async
    public void populateProviderSearchStrCache(String providerSearchStr) {
        try {
            raProvDetailsRepository.findByProviderSearchStr(providerSearchStr);
        } catch (Exception ex) {
            log.error("Error in populateProviderSearchStrCache providerSearchStr {} ex {}", providerSearchStr, ex.getMessage());
        }
    }

    public ConcurrentLruCache<String, List<RAFileDetails>> marketSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByMarketSearchStr(p);
    });
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

    public ConcurrentLruCache<String, List<RAFileDetails>> lineOfBusinessSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByLineOfBusinessSearchStr(p);
    });
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

    public List<RAFileDetails> getRosterSourceListFromMarketAndState(String market, String lineOfBusiness) {
        return raFileDetailsRepository.findByMarketAndLineOfBusiness(market, lineOfBusiness);
    }

    public List<RAFileDetails> getRosterSourceListFromMarket(String market) {
        return raFileDetailsRepository.findByMarket(market);
    }

    public List<RAFileDetails> getRosterSourceListFromLineOfBusiness(String lineOfBusiness) {
        return raFileDetailsRepository.findByLineOfBusiness(lineOfBusiness);
    }

    @Async
    public void populateLineOfBusinessSearchStrCache(String lineOfBusinessSearchStr) {
        try {
            raFileDetailsRepository.findByLineOfBusinessSearchStr(lineOfBusinessSearchStr);
        } catch (Exception ex) {
            log.error("Error in populateLineOfBusinessSearchStrCache lineOfBusinessSearchStr {} ex {}", lineOfBusinessSearchStr, ex.getMessage());
        }
    }

    List<String> allMarkets = null;
    public List<String> findAllMarkets() {
        if (allMarkets == null) {
            allMarkets = raProvMarketLobMapRepository.findAllMarkets();
        }
        return allMarkets;
    }

    private List<String> allLineOfBusiness = null;
    public List<String> findAllLineOfBusiness() {
        if (allLineOfBusiness == null) {
            allLineOfBusiness = raProvMarketLobMapRepository.findAllLineOfBusinesses();
        }
        return allLineOfBusiness;
    }

    public List<RAFileDetails> getRAProvListFromSearchStr(String searchStr) {
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

package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsLobRepository;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.rostertracker.dto.ListResponse;
import com.hilabs.rostertracker.dto.RAFileDetailsWithSheets;
import com.hilabs.rostertracker.model.RosterFilterType;
import com.hilabs.rostertracker.utils.RosterUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import java.util.*;
import java.util.stream.Collectors;

import static com.hilabs.roster.util.FileUtils.getAdjustedString;
import static com.hilabs.rostertracker.utils.SheetTypeUtils.*;

@Service
@Log4j2
public class RAFileDetailsService {

    @Autowired
    RAFileDetailsRepository raFileDetailsRepository;

    @Autowired
    RASheetDetailsRepository raSheetDetailsRepository;

    @Autowired
    RosterStageService rosterStageService;

    @Autowired
    private RARCRosterISFMapService rarcRosterISFMapService;
    public ConcurrentLruCache<String, List<RAFileDetails>> marketSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByMarketSearchStr(p);
    });
    public ConcurrentLruCache<String, List<RAFileDetails>> lineOfBusinessSearchStrCache = new ConcurrentLruCache<>(10000, (p) -> {
        return raFileDetailsRepository.findByLineOfBusinessSearchStr(p);
    });

    @Autowired
    RAFileDetailsLobRepository raFileDetailsLobRepository;

//    public RAFileDetailsListAndSheetList getRosterSourceListAndFilesList(Long raFileDetailsId, String market, String lineOfBusiness,
//                                                                         long startTime, long endTime, int limit, int offset, List<Integer> statusCodes) {
//        List<RAFileDetails> raFileDetailsList = getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness, startTime, endTime, statusCodes, limit, offset);
//        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream().map(RAFileDetails::getId).collect(Collectors.toList()));
//        return new RAFileDetailsListAndSheetList(raFileDetailsList, raSheetDetailsList);
//    }

//    public ListResponse<RAFileDetails> getRAFileDetailsList(Long raFileDetailsId, String market, String lineOfBusiness, long startTime, long endTime,
//                                                            List<Integer> statusCodes, int limit, int offset) {
//        if (raFileDetailsId != null && raFileDetailsId > 0) {
//            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(raFileDetailsId);
//            if (optionalRAFileDetails.isPresent()) {
//                return new ListResponse<RAFileDetails>(Collections.singletonList(optionalRAFileDetails.get()), 1L);
//            }
//        }
//        Date startDate = new Date(startTime);
//        Date endDate = new Date(endTime);
//        return getRAFileDetailsList(market, lineOfBusiness, startDate, endDate, statusCodes, limit, offset);
//    }

    public ListResponse<RAFileDetailsWithSheets> getRAFileDetailsWithSheetsList(String fileName, String plmTicketId, String market, String lineOfBusiness, long startTime, long endTime,
                                                            List<Integer> statusCodes, int limit, int offset, boolean onlyDataSheets, int minSheetCount, boolean alsoConsiderEmptySheets) {
        List<String> types = onlyDataSheets ? dataTypeList : allTypeList;
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        return getRAFileDetailsWithSheetsList(fileName, plmTicketId, market, lineOfBusiness, startDate, endDate,
                statusCodes, limit, offset, types, minSheetCount, alsoConsiderEmptySheets);
    }

    public ListResponse<RAFileDetailsWithSheets> getRAFileDetailsWithSheetsList(String fileName, String plmTicketId, String market, String lineOfBusiness, Date startDate, Date endDate,
                                                    List<Integer> statusCodes, int limit, int offset, List<String> types,
                                                                                int minSheetCount, boolean alsoConsiderEmptySheets) {
        List<RAFileDetails> raFileDetailsList = new ArrayList<>();
        Integer count = 0;
        //TODO handle limit and offset
        if ((market != null && !market.isEmpty()) && (lineOfBusiness != null && !lineOfBusiness.isEmpty())) {
            raFileDetailsList = raFileDetailsRepository.findByMarketAndLineOfBusiness(fileName, plmTicketId, market, lineOfBusiness, startDate,
                    endDate, statusCodes, limit, offset, types, minSheetCount);
            count = raFileDetailsRepository.countByMarketAndLineOfBusiness(fileName, plmTicketId, market, lineOfBusiness, startDate,
                    endDate, statusCodes, types, minSheetCount);
        } else if (market != null && !market.isEmpty()) {
            raFileDetailsList = raFileDetailsRepository.findByMarket(fileName, plmTicketId, market, startDate, endDate, statusCodes, limit, offset, types, minSheetCount);
            count = raFileDetailsRepository.countByMarket(fileName, plmTicketId, market, startDate, endDate, statusCodes, types, minSheetCount);
        } else if (lineOfBusiness != null && !lineOfBusiness.isEmpty()) {
            raFileDetailsList = raFileDetailsRepository.findByLineOfBusiness(fileName, plmTicketId, lineOfBusiness, startDate, endDate, statusCodes, limit, offset, types, minSheetCount);
            count = raFileDetailsRepository.countByLineOfBusiness(fileName, plmTicketId, lineOfBusiness, startDate, endDate, statusCodes, types, minSheetCount);
        } else {
            raFileDetailsList =  raFileDetailsRepository.findRAFileDetailsListBetweenDates(fileName, plmTicketId, startDate, endDate, statusCodes,
                    limit, offset, types, minSheetCount);
            count = raFileDetailsRepository.countRAFileDetailsListBetweenDates(fileName, plmTicketId, startDate, endDate, statusCodes, types, minSheetCount);
        }
        List<RASheetDetails> raSheetDetailsListAll = raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream()
                .map(RAFileDetails::getId).collect(Collectors.toList()), types);
        List<RASheetDetails> raSheetDetailsList = new ArrayList<>();
        if (!alsoConsiderEmptySheets) {
            for (RASheetDetails raSheetDetails : raSheetDetailsListAll) {
                if (isDataSheet(raSheetDetails.getType()) && (raSheetDetails.getRosterRecordCount() == null || raSheetDetails.getRosterRecordCount() == 0)) {
                    continue;
                }
                raSheetDetailsList.add(raSheetDetails);
            }
        } else {
            raSheetDetailsList = raSheetDetailsListAll;
        }
        return new ListResponse<RAFileDetailsWithSheets>(getRAFileDetailsWithSheets(raFileDetailsList, raSheetDetailsList),
                new Long(count));
    }

    public static List<RAFileDetailsWithSheets> getRAFileDetailsWithSheets(List<RAFileDetails> raFileDetailsList,
                                                                           List<RASheetDetails> raSheetDetailsList) {
        Map<Long, List<RASheetDetails>> raSheetDetailsListMap = new HashMap<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            raSheetDetailsListMap.putIfAbsent(raSheetDetails.getRaFileDetailsId(), new ArrayList<>());
            raSheetDetailsListMap.get(raSheetDetails.getRaFileDetailsId()).add(raSheetDetails);
        }
        List<RAFileDetailsWithSheets> raFileDetailsWithSheetsList = new ArrayList<>();
        for (RAFileDetails raFileDetails : raFileDetailsList) {
            raFileDetailsWithSheetsList.add(new RAFileDetailsWithSheets(raFileDetails, raSheetDetailsListMap.getOrDefault(raFileDetails.getId(),
                    Collections.emptyList())));
        }
        return raFileDetailsWithSheetsList;
    }

    public Optional<RAFileDetails> findRAFileDetailsById(Long rosterFileId) {
        return raFileDetailsRepository.findById(rosterFileId);
    }

    public List<String> findByFileSearchStr(String providerSearchStr, List<Integer> statusCodes) {
        return raFileDetailsRepository.findByFileSearchStr(providerSearchStr, statusCodes);
    }

    public List<String> findByPlmSearchStr(String plmSearchStr, List<Integer> statusCodes) {
        return raFileDetailsRepository.findByPlmSearchStr(plmSearchStr, statusCodes);
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

//    public ListResponse<RAFileDetails> getRosterSourceListFromMarketAndState(String market, String lineOfBusiness, Date startDate,
//                                                                     Date endDate, List<Integer> statusCodes, List<String> types,
//                                                                     int limit, int offset) {
//        List<RAFileDetails> raFileDetailsList = raFileDetailsRepository.findByMarketAndLineOfBusiness(market, lineOfBusiness, startDate, endDate, statusCodes, types, limit, offset);
//    }

//    public List<RAFileDetails> getRosterSourceListFromMarket(String market, Date startDate, Date endDate,
//                                                             List<Integer> statusCodes, int limit, int offset) {
//        return raFileDetailsRepository.findByMarket(market, startDate, endDate, statusCodes, limit, offset);
//    }

//    public List<RAFileDetails> getRosterSourceListFromLineOfBusiness(String lineOfBusiness, Date startDate, Date endDate,
//                                                                     List<Integer> statusCodes, int limit, int offset) {
//        return raFileDetailsRepository.findByLineOfBusiness(lineOfBusiness, startDate, endDate, statusCodes, limit, offset);
//    }

    @Async
    public void populateLineOfBusinessSearchStrCache(String lineOfBusinessSearchStr) {
        try {
            raFileDetailsRepository.findByLineOfBusinessSearchStr(lineOfBusinessSearchStr);
        } catch (Exception ex) {
            log.error("Error in populateLineOfBusinessSearchStrCache lineOfBusinessSearchStr {} ex {}", lineOfBusinessSearchStr, ex.getMessage());
        }
    }

    public List<String> findAllMarkets(List<Integer> statusCodes) {
        return raFileDetailsRepository.findAllMarkets(statusCodes);
    }

    public List<String> findAllLineOfBusiness() {
        return raFileDetailsLobRepository.findAllLineOfBusinesses();
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

    public List<Integer> getStatusCodes(RosterFilterType rosterFilterType) {
        if (rosterFilterType == null) {
            return rosterStageService.getNonFailedFileStatusCodes();
        } else if (rosterFilterType == RosterFilterType.ROSTER_TRACKER) {
            return rosterStageService.getNonFailedFileStatusCodes();
        } else if (rosterFilterType == RosterFilterType.ERROR_REPORTING) {
            return rosterStageService.getNonFailedFileStatusCodes();
        } else if (rosterFilterType == RosterFilterType.CONFIGURATOR) {
            List<Integer> statusCodes = rosterStageService.getNonFailedFileStatusCodes();
            return statusCodes.stream().filter(p -> p >= 27).collect(Collectors.toList());
        } else if (rosterFilterType == RosterFilterType.NON_COMPATIBLE) {
            return rosterStageService.getFailedFileStatusCodes();
        }
        return rosterStageService.getNonFailedFileStatusCodes();
    }

    public void updateManualActionRequiredInRAFileDetails(Long raFileDetailsId, Integer manualActionRequired) {
        raFileDetailsRepository.updateManualActionRequiredInRAFileDetails(raFileDetailsId, manualActionRequired);
    }
}

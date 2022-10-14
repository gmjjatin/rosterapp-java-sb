package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.rostertracker.dto.SheetDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RASheetDetailsService {
    @Autowired
    RAFileDetailsRepository raFileDetailsRepository;

    @Autowired
    RASheetDetailsRepository raSheetDetailsRepository;

    @Autowired
    private RARCRosterISFMapService rarcRosterISFMapService;

    public Optional<RASheetDetails> findRASheetDetailsById(long raSheetDetailsId) {
        return raSheetDetailsRepository.findById(raSheetDetailsId);
    }

    public List<RASheetDetails> findRASheetDetailsListForFileIdsList(List<Long> raFileDetailsIds, boolean onlyAutomatedAndTerms) {
        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsIds);
        if (onlyAutomatedAndTerms) {
            return filterAutomatedAndTermRASheetDetailsList(raSheetDetailsList);
        }
        return raSheetDetailsList;
    }

    public List<RASheetDetails> getRASheetDetailsList(Long raFileDetailsId) {
        log.debug("Fetch sheet data for raFileDetailsId : {}", raFileDetailsId);
        return raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId);
    }

    public List<SheetDetails> getAllSheetDetailsWithColumnMappingList(Long raFileDetailsId) {
        List<RASheetDetails> raSheetDetailsList = getRASheetDetailsList(raFileDetailsId);
        List<SheetDetails> sheetDetailsList = new ArrayList<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            //TODO demo check if mapping is available
            int count = rarcRosterISFMapService.countMappingCountForSheetDetailsId(raSheetDetails.getId());
            sheetDetailsList.add(new SheetDetails(raSheetDetails.getId(), raSheetDetails.getTabName(),
                    raSheetDetails.getType(), raSheetDetails.getType(), count != 0));
        }
        return sheetDetailsList;
    }

    private static List<RASheetDetails> filterAutomatedAndTermRASheetDetailsList(List<RASheetDetails> raSheetDetailsList) {
        List<RASheetDetails> filteredRaSheetDetailsList = new ArrayList<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            if (raSheetDetails.getType() == null) {
                continue;
            }
            //TODO demo
            List<String> automatedAndTermRASheetTypes = Arrays.asList("NON_TERM", "TERM", "Automated", "Data");
            String type = raSheetDetails.getType();
            if (automatedAndTermRASheetTypes.stream().anyMatch(p -> p.equalsIgnoreCase(type))) {
                filteredRaSheetDetailsList.add(raSheetDetails);
            }
        }
        return filteredRaSheetDetailsList;
    }
}

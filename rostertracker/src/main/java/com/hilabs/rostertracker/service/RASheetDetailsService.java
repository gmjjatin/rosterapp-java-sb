package com.hilabs.rostertracker.service;

import com.hilabs.roster.dto.ContactType;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RARTContactDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RARTContactDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.rostertracker.dto.RASheetReport;
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
    RARTContactDetailsRepository rartContactDetailsRepository;

    @Autowired
    private RARCRosterISFMapService rarcRosterISFMapService;

    public Optional<RASheetDetails> findRASheetDetailsById(long raSheetDetailsId) {
        return raSheetDetailsRepository.findById(raSheetDetailsId);
    }

//    public List<RASheetDetails> findRASheetDetailsListForFileIdsList(List<Long> raFileDetailsIds, boolean onlyAutomatedAndTerms) {
//        List<RASheetDetails> raSheetDetailsList = raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsIds);
//        if (onlyAutomatedAndTerms) {
//            return filterAutomatedAndTermRASheetDetailsList(raSheetDetailsList);
//        }
//        return raSheetDetailsList;
//    }

    public List<RASheetDetails> getRASheetDetailsList(Long raFileDetailsId, List<String> types) {
        log.debug("Fetch sheet data for raFileDetailsId : {}", raFileDetailsId);
        return raSheetDetailsRepository.getSheetDetailsForAFileId(raFileDetailsId, types);
    }

    public List<SheetDetails> getAllSheetDetailsWithColumnMappingList(Long raFileDetailsId, List<String> types) {
        List<RASheetDetails> raSheetDetailsList = getRASheetDetailsList(raFileDetailsId, types);
        List<SheetDetails> sheetDetailsList = new ArrayList<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            //TODO demo check if mapping is available
            int count = rarcRosterISFMapService.countMappingCountForSheetDetailsId(raSheetDetails.getId());
            sheetDetailsList.add(new SheetDetails(raSheetDetails.getId(), raSheetDetails.getTabName(),
                    raSheetDetails.getType(), raSheetDetails.getType(), count != 0));
        }
        return sheetDetailsList;
    }


    public RASheetReport getRASheetReport(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        RASheetReport raSheetReport = new RASheetReport();
        List<RARTContactDetails> rartContactDetailsList = rartContactDetailsRepository.findRARTContactDetailsByFileDetailsId(raFileDetails.getId());
        Optional<RARTContactDetails> apdoRARTContactDetailsOptional = rartContactDetailsList.stream().filter(p -> p.getContactType() != null
                && p.getContactType().equals(ContactType.APDO_CONTACT.name())).findFirst();
        Optional<RARTContactDetails> peRARTContactDetailsOptional = rartContactDetailsList.stream().filter(p -> p.getContactType() != null
                && p.getContactType().equals(ContactType.PE_CONTACT.name())).findFirst();
        raSheetReport.setApdoContact(apdoRARTContactDetailsOptional.isPresent() ? apdoRARTContactDetailsOptional.get().getContact() : "-");
        raSheetReport.setPeContact(peRARTContactDetailsOptional.isPresent() ? peRARTContactDetailsOptional.get().getContact() : "-");
        raSheetReport.setMarket(raFileDetails.getMarket());
        //TODO tables identified
        raSheetReport.setTablesIdentifiedInRosterSheetCount(1);
        raSheetReport.setRosterRecordCount(raSheetDetails.getRosterRecordCount());
        raSheetReport.setIsfRowCount(raSheetDetails.getIsfRowCount());
        raSheetReport.setDartRowCount(raSheetDetails.getOutRowCount());
        raSheetReport.setSpsLoadTransactionCount(raSheetDetails.getTargetLoadTransactionCount());
//        raSheetReport.setSuccessCount();
//        raSheetReport.setWarningCount();
//        raSheetReport.setFailedCount();
        raSheetReport.setSpsLoadSuccessTransactionCount(raSheetDetails.getTargetLoadSuccessTransactionCount());
        return raSheetReport;
    }
}

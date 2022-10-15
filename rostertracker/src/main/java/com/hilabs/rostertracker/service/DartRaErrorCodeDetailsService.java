package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.DartRaErrorCodeDetails;
import com.hilabs.roster.repository.DartRaErrorCodeDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DartRaErrorCodeDetailsService {
    @Autowired
    private DartRaErrorCodeDetailsRepository dartRaErrorCodeDetailsRepository;


    public ConcurrentLruCache<String, List<DartRaErrorCodeDetails>> findByErrorCodeCache = new ConcurrentLruCache<>(10000, (errorCode) -> {
        return dartRaErrorCodeDetailsRepository.findByErrorCode(errorCode);
    });
    public Optional<DartRaErrorCodeDetails> findByErrorCode(String errorCode) {
        try {
            List<DartRaErrorCodeDetails> errorCodeDetailsList = findByErrorCodeCache.get(errorCode);
            return errorCodeDetailsList.size() > 0 ? Optional.of(errorCodeDetailsList.get(0)) : Optional.empty();
        } catch (Exception ex) {
            List<DartRaErrorCodeDetails> errorCodeDetailsList = dartRaErrorCodeDetailsRepository.findByErrorCode(errorCode);
            return errorCodeDetailsList.size() > 0 ? Optional.of(errorCodeDetailsList.get(0)) : Optional.empty();
        }
    }


    //TODO
    public static class ErrorCodesAndDescription {
        public List<String> errorCodes;
        public String errorDescription;
        public ErrorCodesAndDescription(List<String> errorCodes, String errorDescription) {
            this.errorCodes = errorCodes;
            this.errorDescription = errorDescription;
        }
    }

    public ErrorCodesAndDescription getErrorString(List<String> fileErrorCodes, List<String> sheetErrorCodes) {
        List<String> ignoreErrorCategories = Arrays.asList("NEED_NOT_BE_PROCESSED",
                "CAN_BE_PROCESSED",
                "NEED_TO_BE_PROCESSESED_SEPARATELY",
                "NEED_TO_BE_PROCESSED_SEPARATELY");
        List<String> errorList = new ArrayList<>();
        List<String> errorCodes = new ArrayList<>();
        for (String fileErrorCode : fileErrorCodes) {
            Optional<DartRaErrorCodeDetails> optionalDartRaErrorCodeDetails = findByErrorCode(fileErrorCode);
            if (!optionalDartRaErrorCodeDetails.isPresent()) {
                continue;
            }
            DartRaErrorCodeDetails dartRaErrorCodeDetails = optionalDartRaErrorCodeDetails.get();
            if (dartRaErrorCodeDetails.getErrorCodeCategory() == null) {
                continue;
            }
            if (ignoreErrorCategories.stream().anyMatch(p -> p.equalsIgnoreCase(dartRaErrorCodeDetails.getErrorCodeCategory()))) {
                continue;
            }
            errorCodes.add(fileErrorCode);
            errorList.add(dartRaErrorCodeDetails.getErrorCodeDescription());
        }
        for (String sheetErrorCode : sheetErrorCodes) {
            Optional<DartRaErrorCodeDetails> optionalDartRaErrorCodeDetails = findByErrorCode(sheetErrorCode);
            if (!optionalDartRaErrorCodeDetails.isPresent()) {
               continue;
            }
            DartRaErrorCodeDetails dartRaErrorCodeDetails = optionalDartRaErrorCodeDetails.get();
            if (dartRaErrorCodeDetails.getErrorCodeCategory() == null) {
                continue;
            }
            if (ignoreErrorCategories.stream().anyMatch(p -> p.equalsIgnoreCase(dartRaErrorCodeDetails.getErrorCodeCategory()))) {
                continue;
            }
            errorCodes.add(sheetErrorCode);
            errorList.add(dartRaErrorCodeDetails.getErrorCodeDescription());
        }
        return new ErrorCodesAndDescription(errorCodes, String.join(", ", errorList));
    }
}

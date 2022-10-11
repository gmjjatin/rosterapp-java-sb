package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.DartRaErrorCodeDetails;
import com.hilabs.roster.repository.DartRaErrorCodeDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class DartRaErrorCodeDetailsService {
    @Autowired
    private DartRaErrorCodeDetailsRepository dartRaErrorCodeDetailsRepository;

    public Optional<DartRaErrorCodeDetails> findByErrorCode(String errorCode) {
        List<DartRaErrorCodeDetails> errorCodeDetailsList = dartRaErrorCodeDetailsRepository.findByErrorCode(errorCode);
        return errorCodeDetailsList.size() > 0 ? Optional.of(errorCodeDetailsList.get(0)) : Optional.empty();
    }

    public String getErrorString(List<String> errorCodes) {
        StringBuilder error = new StringBuilder();
        for (String errorCode : errorCodes) {
            Optional<DartRaErrorCodeDetails> optionalDartRaErrorCodeDetails = findByErrorCode(errorCode);
            optionalDartRaErrorCodeDetails.ifPresent(dartRaErrorCodeDetails -> error.append(dartRaErrorCodeDetails.getErrorCodeDescription()));
        }
        return error.toString();
    }
}

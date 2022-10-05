package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAStatusCDMaster;
import com.hilabs.roster.repository.RAStatusCDMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.hilabs.roster.util.Constants.*;

@Service
public class RAStatusService {

    @Autowired
    private RAStatusCDMasterRepository raStatusCDMasterRepository;
    public static List<Integer> getStatusCodes(boolean isCompatible) {
        if (isCompatible) {
            return Arrays.asList(ROSTER_INGESTION_COMPLETED);
        } else {
            return Arrays.asList(ROSTER_INGESTION_VALIDATION_FAILED, ROSTER_INGESTION_FAILED);
        }
    }
    public String getDisplayStatus(Integer statusCode) {
        if (statusCode == null) {
            return "-";
        }
        Optional<RAStatusCDMaster> optionalRAStatusCDMaster =  raStatusCDMasterRepository.getRAStatusCDMasterListForCode(statusCode);
        if (!optionalRAStatusCDMaster.isPresent() || optionalRAStatusCDMaster.get().getBusinessStatus() == null) {
            return "-";
        }
        return optionalRAStatusCDMaster.get().getBusinessStatus();
    }
}

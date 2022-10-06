package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAStatusCDMaster;
import com.hilabs.roster.repository.RAStatusCDMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RAStatusService {

    @Autowired
    private RAStatusCDMasterRepository raStatusCDMasterRepository;
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

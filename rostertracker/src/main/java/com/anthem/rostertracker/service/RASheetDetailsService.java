package com.anthem.rostertracker.service;

import com.anthem.rostertracker.entity.RAFileDetails;
import com.anthem.rostertracker.entity.RAProvDetails;
import com.anthem.rostertracker.entity.RASheetDetails;
import com.anthem.rostertracker.model.RAFileDetailsListAndSheetList;
import com.anthem.rostertracker.repository.RAFileDetailsRepository;
import com.anthem.rostertracker.repository.RASheetDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RASheetDetailsService {
    @Autowired
    RAProviderService raProviderService;

    @Autowired
    RAFileDetailsRepository raFileDetailsRepository;

    @Autowired
    RASheetDetailsRepository raSheetDetailsRepository;

    public Optional<RASheetDetails> findRASheetDetailsById(long raSheetDetailsId) {
        return raSheetDetailsRepository.findById(raSheetDetailsId);
    }
}

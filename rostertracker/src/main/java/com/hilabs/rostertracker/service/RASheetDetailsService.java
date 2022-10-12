package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
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

    public Optional<RASheetDetails> findRASheetDetailsById(long raSheetDetailsId) {
        return raSheetDetailsRepository.findById(raSheetDetailsId);
    }

    public List<RASheetDetails> findRASheetDetailsListForFileIdsList(List<Long> raFileDetailsIds) {
        return raSheetDetailsRepository.findRASheetDetailsListForFileIdsList(raFileDetailsIds);
    }
}

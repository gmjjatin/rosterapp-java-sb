package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RASheetDetailsService {
    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;

    public void updateRASheetDetailsStatus(Long raSheetDetailsId, Integer status) {
        raSheetDetailsRepository.updateRASheetDetailsStatus(raSheetDetailsId, status);
    }

    public Optional<RASheetDetails> findByRASheetDetailsId(Long raSheetDetailsId) {
        return raSheetDetailsRepository.findById(raSheetDetailsId);
    }
}

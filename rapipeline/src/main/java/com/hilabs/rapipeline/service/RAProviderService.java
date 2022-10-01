package com.hilabs.rapipeline.service;

import com.hilabs.roster.entity.RAProvDetails;
import com.hilabs.roster.repository.RAProvDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class RAProviderService {
    @Autowired
    private RAProvDetailsRepository raProvDetailsRepository;

    public Optional<RAProvDetails> findByProvider(String provider) {
        return raProvDetailsRepository.findByProvider(provider);
    }
}

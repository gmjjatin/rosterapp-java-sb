package com.rapipeline.service;

import com.rapipeline.entity.RAProvDetails;
import com.rapipeline.repository.RAFileDetailsRepository;
import com.rapipeline.repository.RAProvDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import java.util.List;
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

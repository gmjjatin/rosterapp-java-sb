package com.hilabs.rapipeline.config;

import com.hilabs.mcheck.config.BatchConfig;
import com.hilabs.mcheck.exception.ApplicationException;
import com.hilabs.rapipeline.ingestion.IngestionFetcher;
import com.hilabs.rapipeline.dart.DartFetcher;
import com.hilabs.rapipeline.isf.IsfFetcher;
import com.hilabs.rapipeline.preprocessing.PreProcessingFetcher;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class AppConfig {

    @Autowired
    private IngestionFetcher ingestionFetcher;

    @Autowired
    private PreProcessingFetcher preProcessingFetcher;

    @Autowired
    private IsfFetcher isfFetcher;

    @Autowired
    private DartFetcher dartFetcher;

    @Value("${ingestionConfigPath}")
    private String ingestionConfigPath;

    @Value("${preProcessingConfigPath}")
    private String preProcessingConfigPath;

    @Value("${isfConfigPath}")
    private String isfConfigPath;

    @Value("${dartConfigPath}")
    private String dartConfigPath;

    @PostConstruct
    public void initialize() throws SchedulerException, ApplicationException {
        log.info("Initiate the scheduler");

        new BatchConfig(ingestionConfigPath)
                .registerJobRetrievers(ingestionFetcher)
                .build();

        new BatchConfig(preProcessingConfigPath)
                .registerJobRetrievers(preProcessingFetcher)
                .build();

        new BatchConfig(isfConfigPath)
                .registerJobRetrievers(isfFetcher)
                .build();

        new BatchConfig(dartConfigPath)
                .registerJobRetrievers(dartFetcher)
                .build();
    }
}

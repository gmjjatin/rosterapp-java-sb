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

    @PostConstruct
    public void initialize() throws SchedulerException, ApplicationException {
        log.info("Initiate the scheduler");

        new BatchConfig("./config.json")
                .registerJobRetrievers(ingestionFetcher)
                .build();

        new BatchConfig("./config.json")
                .registerJobRetrievers(preProcessingFetcher)
                .build();

        new BatchConfig("./config.json")
                .registerJobRetrievers(isfFetcher)
                .build();

//        new BatchConfig("./config.json")
//                .registerJobRetrievers(dartFetcher)
//                .build();
    }
}

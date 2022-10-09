package com.hilabs.rapipeline.config;

import com.hilabs.mcheck.config.BatchConfig;
import com.hilabs.mcheck.exception.ApplicationException;
import com.hilabs.rapipeline.ingestion.IngestionFetcher;
import com.hilabs.rapipeline.preprocessing.PreProcessingFirstJobFetcher;
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
    private PreProcessingFirstJobFetcher preProcessingFirstJobFetcher;

    @PostConstruct
    public void initialize() throws SchedulerException, ApplicationException {
        log.info("Initiate the scheduler");

        new BatchConfig("./config.json")
//                .registerJobRetrievers(ingestionFetcher)
                .registerJobRetrievers(preProcessingFirstJobFetcher)
                .build();
    }
}

package com.hilabs.rapipeline.config;

import com.hilabs.mcheck.config.BatchConfig;
import com.hilabs.mcheck.exception.ApplicationException;
import com.hilabs.rapipeline.dartui.DartUIFetcher;
import com.hilabs.rapipeline.ingestion.IngestionFetcher;
import com.hilabs.rapipeline.dart.DartFetcher;
import com.hilabs.rapipeline.isf.IsfFetcher;
import com.hilabs.rapipeline.preprocessing.PreProcessingFetcher;
import com.hilabs.rapipeline.sps.SpsFetcher;
import com.hilabs.rapipeline.test.TestFetcher;
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
    private TestFetcher testFetcher;

    @Autowired
    private IngestionFetcher ingestionFetcher;

    @Autowired
    private PreProcessingFetcher preProcessingFetcher;

    @Autowired
    private IsfFetcher isfFetcher;

    @Autowired
    private DartFetcher dartFetcher;

    @Autowired
    private DartUIFetcher dartUIFetcher;

    @Autowired
    private SpsFetcher spsFetcher;

    @Value("${ingestionConfigPath}")
    private String ingestionConfigPath;

    @Value("${preProcessingConfigPath}")
    private String preProcessingConfigPath;

    @Value("${isfConfigPath}")
    private String isfConfigPath;

    @Value("${dartConfigPath}")
    private String dartConfigPath;

    @Value("${dartUIConfigPath}")
    private String dartUIConfigPath;

    @Value("${spsConfigPath}")
    private String spsConfigPath;

    @PostConstruct
    public void initialize() throws SchedulerException, ApplicationException {
        log.info("Initiate the scheduler");

//        new BatchConfig("./test-config.json")
//                .registerJobRetrievers(testFetcher)
//                .build();

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

        new BatchConfig(dartUIConfigPath)
                .registerJobRetrievers(dartUIFetcher)
                .build();

        new BatchConfig(spsConfigPath)
                .registerJobRetrievers(spsFetcher)
                .build();

        log.info("Scheduler Initiated");
    }
}

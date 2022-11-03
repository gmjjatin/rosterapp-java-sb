package com.hilabs.rapipeline.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppPropertiesConfig {
    @Value("${raSourceFolder}")
    private String sourceFolder;

    @Value("${raDestinationFolder}")
    private String destinationFolder;

    @Value("${raArchiveFolder}")
    private String archiveFolder;

    @Value("${pythonCommand}")
    private String pythonCommand;

    @Value("${pre_processing_wrapper}")
    private String preProcessingWrapper;

    @Value("${isf_wrapper}")
    private String isfWrapper;

    @Value("${dart_wrapper}")
    private String dartWrapper;

    @Value("${dart_ui_wrapper}")
    private String dartUIWrapper;

    @Value("${sps_wrapper}")
    private String spsWrapper;

    @Value("${envConfigs}")
    private String envConfigs;

    @Value("${rootPath}")
    private String rootPath;
}

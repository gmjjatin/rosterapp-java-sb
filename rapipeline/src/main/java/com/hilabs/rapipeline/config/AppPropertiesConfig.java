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

    @Value("${pre_col_map_norm_launcher}")
    private String preColMapNormLauncher;

    @Value("${pre_norm_col_map_launcher}")
    private String preNormColMapLauncher;

    @Value("${post_col_map_norm_launcher}")
    private String postColMapNormLauncher;

    @Value("${post_norm_col_map_launcher}")
    private String postNormColMapLauncher;

    @Value("${envConfigs}")
    private String envConfigs;

    @Value("${rootPath}")
    private String rootPath;
}

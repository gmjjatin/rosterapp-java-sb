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

    @Value("${pyScriptsFolder}")
    private String pyScriptsFolder;

    @Value("${preProcessPythonFilePath}")
    private String preProcessPythonFilePath;
}

package com.hilabs.rostertracker.config;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Value("${pythonCommand}")
    private String pythonCommand;

    @Value("${envConfigs}")
    private String envConfigs;

    @Value("${restore_wrapper}")
    private String restoreWrapper;
}
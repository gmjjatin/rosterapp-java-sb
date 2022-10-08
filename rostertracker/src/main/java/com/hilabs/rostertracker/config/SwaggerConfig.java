package com.hilabs.rostertracker.config;

import com.hilabs.rostertracker.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    public static final String DEV_TEM = "Roster developers";
    public static final String DEV_TEAM_CONTACT = "shubham.kale@hilabs.com";
    public static final String PRODUCT_LICENSE = "This material is the property of Roster";
    public static final String SWAGGER_VERSION = "1.0";
    public static final String SWAGGER_SERVICE_TERMS = "Terms of service";
    public static final String SWAGGER_LICENSE_URL = "API license URL";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage(Constants.BASE_CONTROLLER_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(Constants.SWAGGER_SERVICE_API, Constants.SWAGGER_SERVICE_OPERATION,
                SWAGGER_VERSION, SWAGGER_SERVICE_TERMS,
                new Contact(DEV_TEM, DEV_TEAM_CONTACT, DEV_TEAM_CONTACT),
                PRODUCT_LICENSE, SWAGGER_LICENSE_URL, Collections.emptyList());
    }
}

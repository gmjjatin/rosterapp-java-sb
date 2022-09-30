package com.rapipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.rapipeline.*", "com.hilabs.roster.entity"})
@ComponentScan({"com.rapipeline.*", "com.hilabs.roster.entity"})
@EntityScan({"com.rapipeline.entity", "com.hilabs.roster.entity"})
public class RAPipelineApplication {
    public static void main(String[] args) {
        SpringApplication.run(RAPipelineApplication.class, args);
    }
}

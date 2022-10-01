package com.hilabs.rapipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.hilabs.*"})
@ComponentScan({"com.hilabs.*"})
@EntityScan({"com.hilabs.*"})
@EnableJpaRepositories("com.hilabs.*")
public class RAPipelineApplication {
    public static void main(String[] args) {
        SpringApplication.run(RAPipelineApplication.class, args);
    }
}

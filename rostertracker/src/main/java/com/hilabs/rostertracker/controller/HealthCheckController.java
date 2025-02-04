package com.hilabs.rostertracker.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/v1")
@CrossOrigin(origins = "*")
public class HealthCheckController {

    @GetMapping("/ping")
    public String healthCheck() {
        return "pong";
    }
}
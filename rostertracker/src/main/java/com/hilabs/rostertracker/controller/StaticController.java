package com.hilabs.rostertracker.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Log4j2
@CrossOrigin(origins = "*")
public class StaticController {
    @RequestMapping("/app/**")
    public String index() {
        return "index";
    }

    @RequestMapping("/")
    public String defaultIndex() {
        return "index";
    }
}

package com.hilabs.rostertracker.controller;

import com.google.gson.Gson;
import com.hilabs.rostertracker.config.ApplicationConfig;
import com.hilabs.rostertracker.model.RestoreRosterRequest;
import com.hilabs.rostertracker.service.PythonInvocationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roster")
@Log4j2
@CrossOrigin(origins = "*")
public class RosterController {
    public static Gson gson = new Gson();

    @Autowired
    private PythonInvocationService pythonInvocationService;

    @Autowired
    private ApplicationConfig applicationConfig;

    @PutMapping("/{rosterId}/restore")
    public ResponseEntity<Map<String, String>> restoreRoster(@RequestBody RestoreRosterRequest restoreRosterRequest, @PathVariable Long rosterId) {
        try {
            File file = new File(applicationConfig.getRestoreWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(),"--envConfigs", applicationConfig.getEnvConfigs(),
                    "--fileDetailsId",  "" + rosterId);
            //TODO return better response
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Error in restoreRoster restoreRosterRequest {} rosterId {}", gson.toJson(restoreRosterRequest), rosterId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }  catch (Exception ex) {
            log.error("Error in restoreRoster restoreRosterRequest {} rosterId {}", gson.toJson(restoreRosterRequest), rosterId);
            throw ex;
        }
    }
}

package com.hilabs.rostertracker.controller;

import com.google.gson.Gson;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.rostertracker.config.ApplicationConfig;
import com.hilabs.rostertracker.dto.SheetIdAndStatusInfo;
import com.hilabs.rostertracker.dto.UpdateStatusRequestElement;
import com.hilabs.rostertracker.model.RestoreRosterRequest;
import com.hilabs.rostertracker.model.TargetPhaseType;
import com.hilabs.rostertracker.service.PythonInvocationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;
    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;

    @PutMapping("/{rosterId}/restore")
    public ResponseEntity<Map<String, String>> restoreRoster(@RequestBody RestoreRosterRequest restoreRosterRequest, @PathVariable Long rosterId) {
        try {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(rosterId);
            if (!optionalRAFileDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("raFileDetails not found with rosterId %s", rosterId));
            }
            TargetPhaseType targetPhaseType = TargetPhaseType.getTargetPhaseTypeFromStr(restoreRosterRequest.getTargetPhase());
            if (targetPhaseType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid targetPhase " + restoreRosterRequest.getTargetPhase());
            }
//            File file = new File(applicationConfig.getRestoreWrapper());
//            pythonInvocationService.invokePythonProcess(file.getPath(), "--envConfigs", applicationConfig.getEnvConfigs(),
//                    "--fileDetailsId", "" + rosterId);
            //TODO return better response
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
//        } catch (IOException ex) {
//            log.error("Error in restoreRoster restoreRosterRequest {} rosterId {}", gson.toJson(restoreRosterRequest), rosterId);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        } catch (Exception ex) {
            log.error("Error in restoreRoster restoreRosterRequest {} rosterId {}", gson.toJson(restoreRosterRequest), rosterId);
            throw ex;
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<Map<String, String>> updateStatus(@RequestBody List<UpdateStatusRequestElement> updateStatusRequestElementList) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            updateStatusRequest(updateStatusRequestElementList, username);
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateStatus updateStatusRequestElementList {} ex {}", gson.toJson(updateStatusRequestElementList),
                    ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    private void updateStatusRequest(List<UpdateStatusRequestElement> updateStatusRequestElementList, String username) {
        Date lastUpdateDate = new Date();
        for (UpdateStatusRequestElement updateStatusRequestElement : updateStatusRequestElementList) {
            Long raFileDetailsId = updateStatusRequestElement.getRaFileDetailsId();
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(raFileDetailsId);
            if (!optionalRAFileDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("raFileDetails not found with raFileDetailsId %s", raFileDetailsId));
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            raFileDetails.setLastUpdatedUserId(username);
            raFileDetails.setLastUpdatedDate(lastUpdateDate);
            raFileDetails.setStatusCode(updateStatusRequestElement.getStatusCode());
            raFileDetailsRepository.save(raFileDetails);
            for (SheetIdAndStatusInfo sheetIdAndStatusInfo : updateStatusRequestElement.getSheetStatsList()) {
                Long raSheetDetailsId = sheetIdAndStatusInfo.getRaSheetDetailsId();
                Optional<RASheetDetails> optionalRASheetDetails = raSheetDetailsRepository.findById(raSheetDetailsId);
                if (!optionalRASheetDetails.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("raSheetDetails not found with raSheetDetailsId %s", raSheetDetailsId));
                }
                RASheetDetails raSheetDetails = optionalRASheetDetails.get();
                raSheetDetails.setStatusCode(sheetIdAndStatusInfo.getStatusCode());
                raSheetDetails.setLastUpdatedUserId(username);
                raSheetDetails.setLastUpdatedDate(lastUpdateDate);
                raSheetDetailsRepository.save(raSheetDetails);
            }
        }
    }
}

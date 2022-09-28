package com.anthem.rostertracker.controller;

import com.anthem.rostertracker.config.RosterConfig;
import com.anthem.rostertracker.dto.*;
import com.anthem.rostertracker.entity.RAFileDetails;
import com.anthem.rostertracker.model.RAFileDetailsListAndSheetList;
import com.anthem.rostertracker.service.RAFalloutReportService;
import com.anthem.rostertracker.service.RAFileDetailsService;
import com.anthem.rostertracker.service.RAFileStatsService;
import com.anthem.rostertracker.utils.LimitAndOffset;
import com.anthem.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.anthem.rostertracker.utils.Utils.getAdjustedStartAndEndTime;
import static com.anthem.rostertracker.utils.Utils.getLimitAndOffsetFromPageInfo;

@RestController
@RequestMapping("/api/v1/error-reporting")
@Log4j2
public class ErrorReportingController {

    @Autowired
    RAFileStatsService raFileStatsService;

    @Autowired
    RAFileDetailsService raFileDetailsService;

    @Autowired
    RAFalloutReportService raFalloutReportService;

    @Autowired
    private RosterConfig rosterConfig;

    @GetMapping("/file-error-stats-list")
    public ResponseEntity<List<RAFileAndErrorStats>> getFileErrorStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                                           @RequestParam(defaultValue = "") String market,
                                                                           @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                           @RequestParam(defaultValue = "-1") Integer providerId,
                                                                           @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                           @RequestParam(defaultValue = "-1") long startTime,
                                                                           @RequestParam(defaultValue = "-1") long endTime) {
        try {
            LimitAndOffset limitAndOffset = getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            RAFileDetailsListAndSheetList raFileDetailsListAndSheetList = raFileDetailsService
                    .getRosterSourceListAndFilesList(raFileDetailsId, providerId, market, lineOfBusiness, startTime, endTime, limit, offset);
            List<RAFileAndErrorStats> raFileAndErrorStatsList = raFileStatsService.getRAFileAndErrorStats(raFileDetailsListAndSheetList);
            return new ResponseEntity<>(raFileAndErrorStatsList, HttpStatus.OK);
        } catch (Exception ex) {
            //TODO fix log
            log.error("Error in getFileErrorStatsList pageNo {} pageSize {} market {} lineOfBusiness {} providerId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, providerId, startTime, endTime);
            throw ex;
        }
    }

    //TODO manikanta fix the API
    @GetMapping("/sheet-error-stats-list")
    public ResponseEntity<List<RASheetAndColumnErrorStats>> getSheetErrorStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                   @RequestParam(defaultValue = "") String market,
                                                                                   @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                                   @RequestParam(defaultValue = "-1") Integer providerId,
                                                                                   @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                                   @RequestParam(defaultValue = "-1") long startTime,
                                                                                   @RequestParam(defaultValue = "-1") long endTime) {
        try {
            LimitAndOffset limitAndOffset = getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            RAFileDetailsListAndSheetList raFileDetailsListAndSheetList = raFileDetailsService
                    .getRosterSourceListAndFilesList(raFileDetailsId, providerId, market, lineOfBusiness, startTime, endTime, limit, offset);
            List<RAFileAndErrorStats> raFileAndErrorStatsList = raFileStatsService.getRAFileAndErrorStats(raFileDetailsListAndSheetList);
            List<RASheetAndColumnErrorStats> raSheetAndColumnErrorStatsList = new ArrayList<>();
            for (RAFileAndErrorStats raFileAndErrorStats : raFileAndErrorStatsList) {
                for (RASheetAndErrorStats raSheetAndErrorStats : raFileAndErrorStats.getSheetStatsList()) {
                    raSheetAndColumnErrorStatsList.add(new RASheetAndColumnErrorStats(raSheetAndErrorStats.getRaSheetDetailsId(), raSheetAndErrorStats.getSheetName()));
                }

            }
            return new ResponseEntity<>(raSheetAndColumnErrorStatsList, HttpStatus.OK);
        } catch (Exception ex) {
            //TODO fix log
            log.error("Error in getFileErrorStatsList pageNo {} pageSize {} market {} lineOfBusiness {} providerId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, providerId, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/sheet-fallout-report")
    public ResponseEntity<RASheetFalloutReport> getRASheetFalloutReport(@RequestParam() Long rosterSheetId) {
        try {
            List<RAFalloutErrorInfo> raFalloutErrorInfoList = raFalloutReportService.getRASheetFalloutReport(rosterSheetId);
            RASheetFalloutReport raSheetFalloutReport = new RASheetFalloutReport();
            raSheetFalloutReport.setRaFalloutErrorInfoList(raFalloutErrorInfoList);
            //TODO manikanta
            return new ResponseEntity<>(raSheetFalloutReport, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRosterFileProgressInfoList rosterSheetId {} - ex {}", rosterSheetId, ex.getMessage());
            throw ex;
        }
    }

    @RequestMapping(path = "/downloadErrorReport", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadErrorReport(@RequestParam() Long rosterFileId) throws IOException {
        try {
            Optional<RAFileDetails> optionalRosterFileDetails = raFileDetailsService.findRAFileDetailsById(rosterFileId);
            if (!optionalRosterFileDetails.isPresent()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_GATEWAY);
            }
            RAFileDetails raFileDetails = optionalRosterFileDetails.get();
            File file = new File(rosterConfig.getDownloadFolder(), raFileDetails.getOriginalFileName());
            return getDownloadFileResponseEntity(file);
        } catch (Exception ex) {
            log.error("Error in downloadErrorReport rosterFileId {} - ex {}", rosterFileId, ex.getMessage());
            throw ex;
        }
    }

    //TODO remove
    @RequestMapping(path = "/downloadSampleReport", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadSampleReport() throws IOException {
        try {
            File file = new File(rosterConfig.getDownloadFolder(), "sample.xls");
            return getDownloadFileResponseEntity(file);
        } catch (Exception ex) {
            log.error("Error in downloadSampleReport - ex {}", ex.getMessage());
            throw ex;
        }
    }
//
    public ResponseEntity<InputStreamResource> getDownloadFileResponseEntity(File file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
}
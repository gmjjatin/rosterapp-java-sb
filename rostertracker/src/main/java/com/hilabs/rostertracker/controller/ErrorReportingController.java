package com.hilabs.rostertracker.controller;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.rostertracker.config.RosterConfig;
import com.hilabs.rostertracker.dto.RAFileAndErrorStats;
import com.hilabs.rostertracker.dto.RASheetAndColumnErrorStats;
import com.hilabs.rostertracker.dto.RASheetAndErrorStats;
import com.hilabs.rostertracker.dto.RASheetFalloutReport;
import com.hilabs.rostertracker.model.RAFileDetailsListAndSheetList;
import com.hilabs.rostertracker.service.RAFalloutReportService;
import com.hilabs.rostertracker.service.RAFileDetailsService;
import com.hilabs.rostertracker.service.RAFileStatsService;
import com.hilabs.rostertracker.utils.LimitAndOffset;
import com.hilabs.rostertracker.utils.Utils;
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

import static com.hilabs.roster.util.RosterStageUtils.getNonFailedFileStatusCodes;

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
                                                                           @RequestParam(defaultValue = "100") Integer pageSize,
                                                                           @RequestParam(defaultValue = "") String market,
                                                                           @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                           @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                           @RequestParam(defaultValue = "-1") long startTime,
                                                                           @RequestParam(defaultValue = "-1") long endTime) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = Utils.getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            RAFileDetailsListAndSheetList raFileDetailsListAndSheetList = raFileDetailsService
                    .getRosterSourceListAndFilesList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, limit, offset, getNonFailedFileStatusCodes());
            List<RAFileAndErrorStats> raFileAndErrorStatsList = raFileStatsService.getRAFileAndErrorStats(raFileDetailsListAndSheetList);
            return new ResponseEntity<>(raFileAndErrorStatsList, HttpStatus.OK);
        } catch (Exception ex) {
            //TODO fix log
            log.error("Error in getFileErrorStatsList pageNo {} pageSize {} market {} lineOfBusiness {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, startTime, endTime);
            throw ex;
        }
    }

    //TODO manikanta fix the API
    @GetMapping("/sheet-error-stats-list")
    public ResponseEntity<List<RASheetAndColumnErrorStats>> getSheetErrorStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                   @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                   @RequestParam(defaultValue = "") String market,
                                                                                   @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                                   @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                                   @RequestParam(defaultValue = "-1") long startTime,
                                                                                   @RequestParam(defaultValue = "-1") long endTime) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = Utils.getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            RAFileDetailsListAndSheetList raFileDetailsListAndSheetList = raFileDetailsService
                    .getRosterSourceListAndFilesList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, limit, offset, getNonFailedFileStatusCodes());
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
            log.error("Error in getFileErrorStatsList pageNo {} pageSize {} market {} lineOfBusiness {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, startTime, endTime);
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
    @RequestMapping(path = "/downloadRoster", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadSampleReport(@RequestParam() Long raFileDetailsId) throws IOException {
        try {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findRAFileDetailsById(raFileDetailsId);
            if (!optionalRAFileDetails.isPresent()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            File file = new File(rosterConfig.getRaArchiveFolder(), raFileDetails.getOriginalFileName());
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
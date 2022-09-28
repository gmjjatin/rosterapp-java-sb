package com.anthem.rostertracker.controller;

import com.anthem.rostertracker.dto.ErrorDescriptionAndCount;
import com.anthem.rostertracker.dto.RAFalloutErrorInfo;
import com.anthem.rostertracker.dto.RAFileAndStats;
import com.anthem.rostertracker.dto.RASheetReport;
import com.anthem.rostertracker.entity.RAFileDetails;
import com.anthem.rostertracker.entity.RASheetDetails;
import com.anthem.rostertracker.model.RAFileDetailsListAndSheetList;
import com.anthem.rostertracker.model.RASheetProgressInfo;
import com.anthem.rostertracker.service.RAFalloutReportService;
import com.anthem.rostertracker.service.RAFileDetailsService;
import com.anthem.rostertracker.service.RAFileStatsService;
import com.anthem.rostertracker.service.RASheetDetailsService;
import com.anthem.rostertracker.utils.LimitAndOffset;
import com.anthem.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.anthem.rostertracker.utils.Utils.getAdjustedStartAndEndTime;
import static com.anthem.rostertracker.utils.Utils.getLimitAndOffsetFromPageInfo;

@RestController
@RequestMapping("/api/v1/progress-tracking")
@Log4j2
public class ProgressTrackingController {
    @Autowired
    RAFileStatsService raFileStatsService;

    @Autowired
    RAFileDetailsService raFileDetailsService;

    @Autowired
    RASheetDetailsService raSheetDetailsService;

    @Autowired
    RAFalloutReportService raFalloutReportService;


    @GetMapping("/file-stats-list")
    public ResponseEntity<List<RAFileAndStats>> getRAProvAndStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
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
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsListAndSheetList);
            return new ResponseEntity<>(raFileAndStatsList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} providerId {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, providerId, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/progress-info-list")
    public ResponseEntity<List<RASheetProgressInfo>> getRosterFileProgressInfoList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                   @RequestParam(defaultValue = "") String market,
                                                                                   @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                                   @RequestParam(defaultValue = "-1") Integer providerId,
                                                                                   @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                                   @RequestParam(name = "startTime", defaultValue = "-1") long startTime,
                                                                                   @RequestParam(name = "endTime", defaultValue = "-1") long endTime) {
        try {
            LimitAndOffset limitAndOffset = getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            RAFileDetailsListAndSheetList raFileDetailsListAndSheetList = raFileDetailsService
                    .getRosterSourceListAndFilesList(raFileDetailsId, providerId, market, lineOfBusiness, startTime, endTime, limit, offset);
            Map<Long, RAFileDetails> raFileDetailsMap = raFileDetailsListAndSheetList.getRAFileDetailsMap();
            List<RASheetProgressInfo> raSheetProgressInfoList = new ArrayList<>();
            for (RASheetDetails raSheetDetails : raFileDetailsListAndSheetList.getRaSheetDetailsList()) {
                raSheetProgressInfoList.add(raFileStatsService.getRASheetProgressInfo(raFileDetailsMap.get(raSheetDetails.getRaFileDetailsId()), raSheetDetails));
            }
            return new ResponseEntity<>(raSheetProgressInfoList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRosterFileProgressInfoList pageNo {} pageSize {} market {} lineOfBusiness {} providerId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, providerId, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/sheet-report")
    public ResponseEntity<RASheetReport> getRASheetReport(@RequestParam() Long rosterSheetId) {
        try {
            Optional<RASheetDetails> optionalRASheetDetails = raSheetDetailsService.findRASheetDetailsById(rosterSheetId);
            if (!optionalRASheetDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "rosterSheetId " + rosterSheetId + " not found");
            }
            RASheetDetails raSheetDetails = optionalRASheetDetails.get();
            List<RAFalloutErrorInfo> raFalloutErrorInfoList = raFalloutReportService.getRASheetFalloutReport(rosterSheetId);
            List<ErrorDescriptionAndCount> errorDescriptionAndCountList = new ArrayList<>();
            for (RAFalloutErrorInfo raFalloutErrorInfo : raFalloutErrorInfoList) {
                errorDescriptionAndCountList.add(new ErrorDescriptionAndCount(raFalloutErrorInfo.getErrorDescription(), raFalloutErrorInfo.getCount()));
            }
            RASheetReport raSheetReport = getRASheetReportObj(raSheetDetails);
            raSheetReport.setErrorDescriptionAndCountList(errorDescriptionAndCountList);
            return new ResponseEntity<>(raSheetReport, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRosterFileProgressInfoList rosterSheetId {} - ex {}", rosterSheetId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error in processing - errorMessage " + ex.getMessage());
        }
    }

    //TODO TEMP method
    public static RASheetReport getRASheetReportObj(RASheetDetails raSheetDetails) {
        RASheetReport raSheetReport = new RASheetReport();
        raSheetReport.setApdoContact("-");
        raSheetReport.setMarket("-");
        raSheetReport.setPeContact("-");
        raSheetReport.setTablesIdentifiedInRosterSheetCount(1);
        raSheetReport.setRosterRecordCount(raSheetDetails.getRosterRecordCount());
        if (raSheetDetails.getName().contains("terms")) {
            return raSheetReport;
        }
        raSheetReport.setIsfRowCount(raSheetDetails.getDartRowCount() / 2);
        raSheetReport.setDartRowCount(raSheetDetails.getDartRowCount());
        raSheetReport.setSpsLoadTransactionCount(raSheetDetails.getSpsLoadTransactionCount());
        raSheetReport.setSuccessCount((int) (raSheetDetails.getSpsLoadTransactionCount() * 0.75));
        raSheetReport.setWarningCount((int) (raSheetDetails.getSpsLoadTransactionCount() * 0.10));
        raSheetReport.setFailedCount((int) (raSheetDetails.getSpsLoadTransactionCount() * 0.15));
        raSheetReport.setSpsLoadSuccessTransactionCount(raSheetDetails.getSpsLoadSuccessTransactionCount());
        return raSheetReport;
    }
}
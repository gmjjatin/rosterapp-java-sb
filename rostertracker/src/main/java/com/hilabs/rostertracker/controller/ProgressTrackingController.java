package com.hilabs.rostertracker.controller;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileErrorCodeDetailRepository;
import com.hilabs.rostertracker.dto.ErrorDescriptionAndCount;
import com.hilabs.rostertracker.dto.InCompatibleRosterDetails;
import com.hilabs.rostertracker.dto.RAFileAndStats;
import com.hilabs.rostertracker.dto.RASheetReport;
import com.hilabs.rostertracker.model.RAFileDetailsListAndSheetList;
import com.hilabs.rostertracker.model.RASheetProgressInfo;
import com.hilabs.rostertracker.service.RAFalloutReportService;
import com.hilabs.rostertracker.service.RAFileDetailsService;
import com.hilabs.rostertracker.service.RAFileStatsService;
import com.hilabs.rostertracker.service.RASheetDetailsService;
import com.hilabs.rostertracker.utils.LimitAndOffset;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.hilabs.roster.util.RosterStageUtils.*;

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

    @Autowired
    RAFileErrorCodeDetailRepository raFileErrorCodeDetailRepository;


    @GetMapping("/file-stats-list")
    public ResponseEntity<List<RAFileAndStats>> getRosterTrackerFileStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
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
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsListAndSheetList);
            return new ResponseEntity<>(raFileAndStatsList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/progress-info-list")
    public ResponseEntity<List<RASheetProgressInfo>> getRosterFileProgressInfoList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                   @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                   @RequestParam(defaultValue = "") String market,
                                                                                   @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                                   @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                                   @RequestParam(name = "startTime", defaultValue = "-1") long startTime,
                                                                                   @RequestParam(name = "endTime", defaultValue = "-1") long endTime) {
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
            Map<Long, RAFileDetails> raFileDetailsMap = raFileDetailsListAndSheetList.getRAFileDetailsMap();
            List<RASheetProgressInfo> raSheetProgressInfoList = new ArrayList<>();
            for (RASheetDetails raSheetDetails : raFileDetailsListAndSheetList.getRaSheetDetailsList()) {
                raSheetProgressInfoList.add(raFileStatsService.getRASheetProgressInfo(raFileDetailsMap.get(raSheetDetails.getRaFileDetailsId()), raSheetDetails));
            }
            return new ResponseEntity<>(raSheetProgressInfoList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRosterFileProgressInfoList pageNo {} pageSize {} market {} lineOfBusiness {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, startTime, endTime);
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
        if (raSheetDetails.getTabName().contains("terms")) {
            return raSheetReport;
        }
        raSheetReport.setIsfRowCount(raSheetDetails.getOutRowCount() / 2);
        raSheetReport.setDartRowCount(raSheetDetails.getOutRowCount());
        raSheetReport.setSpsLoadTransactionCount(raSheetDetails.getTargetLoadTransactionCount());
        raSheetReport.setSuccessCount((int) (raSheetDetails.getTargetLoadTransactionCount() * 0.75));
        raSheetReport.setWarningCount((int) (raSheetDetails.getTargetLoadTransactionCount() * 0.10));
        raSheetReport.setFailedCount((int) (raSheetDetails.getTargetLoadTransactionCount() * 0.15));
        raSheetReport.setSpsLoadSuccessTransactionCount(raSheetDetails.getTargetLoadSuccessTransactionCount());
        return raSheetReport;
    }

    @GetMapping("/non-compatible-file-list")
    public ResponseEntity<List<InCompatibleRosterDetails>> getNonCompatibleFileList(@RequestParam(defaultValue = "1") Integer pageNo,
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
                            startTime, endTime, limit, offset, getFailedFileStatusCodes());
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsListAndSheetList);
            //TODO
            List<InCompatibleRosterDetails> inCompatibleRosterDetails = new ArrayList<>();
            for (RAFileAndStats raFileAndStats : raFileAndStatsList) {
                List<RAFileErrorCodeDetails> raErrorLogs = raFileErrorCodeDetailRepository.findByRAFileDetailsId(raFileAndStats.getRaFileDetailsId());
                //TODO need to fix it
                String errorCode = "RI_ERR_MD_1";
                String error = "-";
                if (raErrorLogs.size() > 0) {
                    errorCode = raErrorLogs.get(0).getErrorCode() != null ? raErrorLogs.get(0).getErrorCode() : errorCode;
                    error = raErrorLogs.get(0).getErrorCodeTemplateParameters();
                }
                InCompatibleRosterDetails details = new InCompatibleRosterDetails(raFileAndStats.getRaFileDetailsId(), raFileAndStats.getFileName(), raFileAndStats.getFileReceivedTime(), raFileAndStats.getRosterRecordCount(),
                        error, errorCode);
                inCompatibleRosterDetails.add(details);
            }
            return new ResponseEntity<>(inCompatibleRosterDetails, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }
}
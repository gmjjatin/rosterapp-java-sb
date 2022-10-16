package com.hilabs.rostertracker.controller;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.entity.RASheetErrorCodeDetails;
import com.hilabs.roster.repository.RAFileErrorCodeDetailRepository;
import com.hilabs.roster.repository.RASheetErrorCodeDetailRepository;
import com.hilabs.rostertracker.dto.*;
import com.hilabs.rostertracker.model.RASheetProgressInfo;
import com.hilabs.rostertracker.service.*;
import com.hilabs.rostertracker.utils.LimitAndOffset;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static com.hilabs.rostertracker.service.RAFileDetailsService.getStatusCodes;

@RestController
@RequestMapping("/api/v1/progress-tracking")
@Log4j2
@CrossOrigin(origins = "*")
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

    @Autowired
    DartRaErrorCodeDetailsService dartRaErrorCodeDetailsService;

    @Autowired
    RASheetErrorCodeDetailRepository raSheetErrorCodeDetailRepository;


    @GetMapping("/file-stats-list")
    public ResponseEntity<CollectionResponse<RAFileAndStats>> getRosterTrackerFileStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
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
            List<RAFileDetails> raFileDetailsList = raFileDetailsService
                    .getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, getStatusCodes("roster-tracker"), limit, offset);
            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream()
                    .map(p -> p.getId()).collect(Collectors.toList()), true);
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsList, raSheetDetailsList);
            CollectionResponse collectionResponse = new CollectionResponse<RAFileAndStats>(pageNo, pageSize, raFileAndStatsList, 1000L);
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/progress-info-list")
    public ResponseEntity<CollectionResponse<RASheetProgressInfo>> getRosterFileProgressInfoList(@RequestParam(defaultValue = "1") Integer pageNo,
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
            List<RAFileDetails> raFileDetailsList = raFileDetailsService
                    .getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, getStatusCodes("roster-tracker"), limit, offset);
            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream()
                    .map(p -> p.getId()).collect(Collectors.toList()), true);
            Map<Long, RAFileDetails> raFileDetailsMap = raFileStatsService.getRAFileDetailsMap(raFileDetailsList);
            List<RASheetProgressInfo> raSheetProgressInfoList = new ArrayList<>();
            for (RASheetDetails raSheetDetails : raSheetDetailsList) {
                raSheetProgressInfoList.add(raFileStatsService.getRASheetProgressInfo(raFileDetailsMap.get(raSheetDetails.getRaFileDetailsId()), raSheetDetails));
            }
            CollectionResponse collectionResponse = new CollectionResponse(pageNo, pageSize, raSheetProgressInfoList, 1000L);
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
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
    public ResponseEntity<CollectionResponse<InCompatibleRosterDetails>> getNonCompatibleFileList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                    @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                    @RequestParam(defaultValue = "") String market,
                                                                                    @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                                    @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                                    @RequestParam(defaultValue = "-1") long startTime,
                                                                                    @RequestParam(defaultValue = "-1") long endTime) {
        try {
            List<Integer> statusCodes = getStatusCodes("non-compatible");
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = Utils.getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            List<RAFileDetails> raFileDetailsList = raFileDetailsService
                    .getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, statusCodes, limit, offset);
            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream()
                    .map(p -> p.getId()).collect(Collectors.toList()), true);
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsList, raSheetDetailsList);
            //TODO
            List<InCompatibleRosterDetails> inCompatibleRosterDetails = new ArrayList<>();
            Map<Long, List<RASheetDetails>> raSheetDetailsListMap = raFileStatsService.getRASheetDetailsListMap(raFileDetailsList, raSheetDetailsList);
            for (RAFileAndStats raFileAndStats : raFileAndStatsList) {
                List<RAFileErrorCodeDetails> raFileErrorCodeDetailsList = raFileErrorCodeDetailRepository.findByRAFileDetailsId(raFileAndStats.getRaFileDetailsId());
                //TODO need to fix it
                List<String> fileErrorCodes = raFileErrorCodeDetailsList.stream().map(p -> p.getErrorCode()).collect(Collectors.toList());
                List<String> sheetErrorCodes = new ArrayList<>();
                if (raSheetDetailsListMap.containsKey(raFileAndStats.getRaFileDetailsId()) && raSheetDetailsListMap.get(raFileAndStats.getRaFileDetailsId()).size() > 0) {
                    for (RASheetDetails raSheetDetails : raSheetDetailsListMap.get(raFileAndStats.getRaFileDetailsId())) {
                        List<RASheetErrorCodeDetails> raSheetErrorCodeDetailsList = raSheetErrorCodeDetailRepository.findByRASheetDetailsId(raSheetDetails.getId());
                        sheetErrorCodes.addAll(raSheetErrorCodeDetailsList.stream().map(RASheetErrorCodeDetails::getErrorCode).collect(Collectors.toList()));
                    }
                }
                sheetErrorCodes = sheetErrorCodes.stream().filter(Objects::nonNull).collect(Collectors.toList());
                DartRaErrorCodeDetailsService.ErrorCodesAndDescription errorCodesAndDescription = dartRaErrorCodeDetailsService.getErrorString(fileErrorCodes, sheetErrorCodes);
                InCompatibleRosterDetails details = new InCompatibleRosterDetails(raFileAndStats.getRaFileDetailsId(), raFileAndStats.getFileName(), raFileAndStats.getFileReceivedTime(),
                        raFileAndStats.getRosterRecordCount(), errorCodesAndDescription.errorDescription,
                        String.join(", ", errorCodesAndDescription.errorCodes));
                inCompatibleRosterDetails.add(details);
            }
            CollectionResponse collectionResponse = new CollectionResponse<InCompatibleRosterDetails>(pageNo, pageSize, inCompatibleRosterDetails, 1000L);
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }
}
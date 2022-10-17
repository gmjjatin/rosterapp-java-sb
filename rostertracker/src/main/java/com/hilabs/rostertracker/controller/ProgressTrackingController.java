package com.hilabs.rostertracker.controller;

import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAFileErrorCodeDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.entity.RASheetErrorCodeDetails;
import com.hilabs.roster.repository.RAFileErrorCodeDetailRepository;
import com.hilabs.roster.repository.RASheetErrorCodeDetailRepository;
import com.hilabs.rostertracker.dto.*;
import com.hilabs.rostertracker.dto.ErrorDescriptionAndCount;
import com.hilabs.rostertracker.dto.InCompatibleRosterDetails;
import com.hilabs.rostertracker.dto.RAFileAndStats;
import com.hilabs.rostertracker.dto.RASheetReport;
import com.hilabs.rostertracker.model.RosterFilterType;
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
import static com.hilabs.rostertracker.service.RAFileStatsService.getFileReceivedTime;

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
            ListResponse<RAFileDetailsWithSheets> raFileDetailsWithSheetsListResponse = raFileDetailsService
                    .getRAFileDetailsWithSheetsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, getStatusCodes(RosterFilterType.ROSTER_TRACKER), limit, offset, true);
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsWithSheetsListResponse.getItems());
            CollectionResponse<RAFileAndStats> collectionResponse = new CollectionResponse<RAFileAndStats>(pageNo, pageSize, raFileAndStatsList,
                    raFileDetailsWithSheetsListResponse.getTotalCount());
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
            ListResponse<RAFileDetailsWithSheets> raFileDetailsWithSheetsListResponse = raFileDetailsService
                    .getRAFileDetailsWithSheetsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, getStatusCodes(RosterFilterType.ROSTER_TRACKER), limit, offset, true);
            List<RASheetProgressInfo> raSheetProgressInfoList = new ArrayList<>();
            for (RAFileDetailsWithSheets raFileDetailsWithSheets : raFileDetailsWithSheetsListResponse.getItems()) {
                RAFileDetails raFileDetails = raFileDetailsWithSheets.getRaFileDetails();
                for (RASheetDetails raSheetDetails : raFileDetailsWithSheets.getRaSheetDetailsList()) {
                    raSheetProgressInfoList.add(raFileStatsService.getRASheetProgressInfo(raFileDetails, raSheetDetails));
                }
            }
            CollectionResponse collectionResponse = new CollectionResponse(pageNo, pageSize, raSheetProgressInfoList, raFileDetailsWithSheetsListResponse.getTotalCount());
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
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findRAFileDetailsById(raSheetDetails.getRaFileDetailsId());
            if (!optionalRAFileDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "raFileDetailsId " + raSheetDetails.getRaFileDetailsId() + " not found");
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            List<RAFalloutErrorInfo> raFalloutErrorInfoList = raFalloutReportService.getRASheetFalloutReport(rosterSheetId);
            List<ErrorDescriptionAndCount> errorDescriptionAndCountList = new ArrayList<>();
            for (RAFalloutErrorInfo raFalloutErrorInfo : raFalloutErrorInfoList) {
                errorDescriptionAndCountList.add(new ErrorDescriptionAndCount(raFalloutErrorInfo.getErrorDescription(), raFalloutErrorInfo.getCount()));
            }
            RASheetReport raSheetReport = raSheetDetailsService.getRASheetReport(raFileDetails, raSheetDetails);
            raSheetReport.setErrorDescriptionAndCountList(errorDescriptionAndCountList);
            return new ResponseEntity<>(raSheetReport, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRosterFileProgressInfoList rosterSheetId {} - ex {}", rosterSheetId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error in processing - errorMessage " + ex.getMessage());
        }
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
            List<Integer> statusCodes = getStatusCodes(RosterFilterType.NON_COMPATIBLE);
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = Utils.getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            ListResponse<RAFileDetailsWithSheets> raFileDetailsWithSheetsListResponse = raFileDetailsService.getRAFileDetailsWithSheetsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, statusCodes, limit, offset, true);
//            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream()
//                    .map(p -> p.getId()).collect(Collectors.toList()), true);
//            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsWithSheetsListResponse.getItems());
            //TODO
            List<InCompatibleRosterDetails> inCompatibleRosterDetails = new ArrayList<>();
            for (RAFileDetailsWithSheets raFileDetailsWithSheets : raFileDetailsWithSheetsListResponse.getItems()) {
                RAFileDetails raFileDetails = raFileDetailsWithSheets.getRaFileDetails();
                List<RAFileErrorCodeDetails> raFileErrorCodeDetailsList = raFileErrorCodeDetailRepository.findByRAFileDetailsId(raFileDetails.getId());
                //TODO need to fix it
                List<String> fileErrorCodes = raFileErrorCodeDetailsList.stream().map(p -> p.getErrorCode()).collect(Collectors.toList());
                List<String> sheetErrorCodes = new ArrayList<>();
                Integer rosterRecordCount = 0;
                for (RASheetDetails raSheetDetails : raFileDetailsWithSheets.getRaSheetDetailsList()) {
                    List<RASheetErrorCodeDetails> raSheetErrorCodeDetailsList = raSheetErrorCodeDetailRepository.findByRASheetDetailsId(raSheetDetails.getId());
                    sheetErrorCodes.addAll(raSheetErrorCodeDetailsList.stream().map(RASheetErrorCodeDetails::getErrorCode).collect(Collectors.toList()));
                    rosterRecordCount += raSheetDetails.getRosterRecordCount();
                }
                sheetErrorCodes = sheetErrorCodes.stream().filter(Objects::nonNull).collect(Collectors.toList());
                DartRaErrorCodeDetailsService.ErrorCodesAndDescription errorCodesAndDescription = dartRaErrorCodeDetailsService.getErrorString(fileErrorCodes, sheetErrorCodes);
                InCompatibleRosterDetails details = new InCompatibleRosterDetails(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                        getFileReceivedTime(raFileDetails), rosterRecordCount,
                        errorCodesAndDescription.errorDescription, String.join(", ", errorCodesAndDescription.errorCodes));
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
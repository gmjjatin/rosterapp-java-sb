package com.hilabs.rostertracker.controller;

import com.hilabs.roster.dto.AltIdType;
import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.repository.RAFileErrorCodeDetailRepository;
import com.hilabs.roster.repository.RASheetErrorCodeDetailRepository;
import com.hilabs.rostertracker.dto.ErrorSummaryElement;
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
            List<RAFileDetails> raFileDetailsList = raFileDetailsService
                    .getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, getStatusCodes(RosterFilterType.ROSTER_TRACKER), limit, offset);
            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream()
                    .map(p -> p.getId()).collect(Collectors.toList()), true);
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsList, raSheetDetailsList);
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
            List<RAFileDetails> raFileDetailsList = raFileDetailsService
                    .getRAFileDetailsList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, getStatusCodes(RosterFilterType.ROSTER_TRACKER), limit, offset);
            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream()
                    .map(p -> p.getId()).collect(Collectors.toList()), true);
            Map<Long, RAFileDetails> raFileDetailsMap = raFileStatsService.getRAFileDetailsMap(raFileDetailsList);
            List<RASheetProgressInfo> raSheetProgressInfoList = new ArrayList<>();
            for (RASheetDetails raSheetDetails : raSheetDetailsList) {
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
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findRAFileDetailsById(raSheetDetails.getRaFileDetailsId());
            if (!optionalRAFileDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "raFileDetailsId " + raSheetDetails.getRaFileDetailsId() + " not found");
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            List<RAFalloutErrorInfo> raFalloutErrorInfoList = raFalloutReportService.getRASheetFalloutReport(rosterSheetId);
            List<ErrorSummaryElement> errorSummaryElementList = new ArrayList<>();
            //TODO demo
//            for (RAFalloutErrorInfo raFalloutErrorInfo : raFalloutErrorInfoList) {
//                errorSummaryElementList.add(new ErrorSummaryElement(raFalloutErrorInfo.getErrorDescription(),
//                        raFalloutErrorInfo.getCount()));
//            }
            RASheetReport raSheetReport = raSheetDetailsService.getRASheetReport(raFileDetails, raSheetDetails);
            raSheetReport.setErrorSummaryElementList(errorSummaryElementList);
            return new ResponseEntity<>(raSheetReport, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRosterFileProgressInfoList rosterSheetId {} - ex {}", rosterSheetId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error in processing - errorMessage " + ex.getMessage());
        }
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
            List<Integer> statusCodes = getStatusCodes(RosterFilterType.NON_COMPATIBLE);
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
            Map<Long, RAFileDetailsLob> raFileDetailsLobMap = raFileStatsService.getRAFileDetailsLobMap(raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList()));
            Map<Long, List<RARTFileAltIds>> rartFileAltIdsListMap = raFileStatsService.getRARTFileAltIdsListMap(raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList()));
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
                String lob = raFileDetailsLobMap.containsKey(raFileAndStats.getRaFileDetailsId()) ? raFileDetailsLobMap.get(raFileAndStats.getRaFileDetailsId()).getLob() : "-";
                List<RARTFileAltIds> rartFileAltIdsList = rartFileAltIdsListMap.containsKey(raFileAndStats.getRaFileDetailsId()) ? rartFileAltIdsListMap
                        .get(raFileAndStats.getRaFileDetailsId()).stream().filter(p -> p.getAltIdType().equals(AltIdType.RO_ID.name())).collect(Collectors.toList()) : new ArrayList<>();
                String plmTicketId = rartFileAltIdsList.size() > 0 ? rartFileAltIdsList.get(0).getAltId() : "-";
                InCompatibleRosterDetails details = new InCompatibleRosterDetails(raFileAndStats.getRaFileDetailsId(), raFileAndStats.getFileName(), raFileAndStats.getFileReceivedTime(),
                        raFileAndStats.getRosterRecordCount(), errorCodesAndDescription.errorDescription,
                        String.join(", ", errorCodesAndDescription.errorCodes), lob, raFileAndStats.getMarket(), plmTicketId);
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
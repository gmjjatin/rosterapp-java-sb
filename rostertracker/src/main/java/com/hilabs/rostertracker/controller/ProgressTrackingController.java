package com.hilabs.rostertracker.controller;

import com.hilabs.roster.dto.AltIdType;
import com.hilabs.roster.dto.RAFalloutErrorInfo;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.repository.RAFileErrorCodeDetailRepository;
import com.hilabs.roster.repository.RASheetErrorCodeDetailRepository;
import com.hilabs.rostertracker.dto.*;
import com.hilabs.rostertracker.model.RASheetProgressInfo;
import com.hilabs.rostertracker.model.RosterFilterType;
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
import static com.hilabs.rostertracker.service.RAFileStatsService.getRosterReceivedTime;
import static com.hilabs.rostertracker.utils.SheetTypeUtils.dataTypeList;
import static com.hilabs.rostertracker.utils.SheetTypeUtils.isDataSheet;

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
    RaErrorCodeDetailsService raErrorCodeDetailsService;

    @Autowired
    RASheetErrorCodeDetailRepository raSheetErrorCodeDetailRepository;


    @GetMapping("/file-stats-list")
    public ResponseEntity<CollectionResponse<RAFileAndStats>> getRosterTrackerFileStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                            @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                            @RequestParam(defaultValue = "") String market,
                                                                                            @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                                            @RequestParam(defaultValue = "") String fileName,
                                                                                            @RequestParam(defaultValue = "") String plmTicketId,
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
                    .getRAFileDetailsWithSheetsList(fileName, plmTicketId, market, lineOfBusiness,
                            startTime, endTime, getStatusCodes(RosterFilterType.ROSTER_TRACKER), limit, offset, true,
                            0, false);
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsWithSheetsListResponse.getItems());
            CollectionResponse<RAFileAndStats> collectionResponse = new CollectionResponse<RAFileAndStats>(pageNo, pageSize, raFileAndStatsList,
                    raFileDetailsWithSheetsListResponse.getTotalCount());
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, fileName, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/progress-info-list")
    public ResponseEntity<List<RASheetProgressInfo>> getRosterFileProgressInfoList(@RequestParam(defaultValue = "1") Long raFileDetailsId) {
        try {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findRAFileDetailsById(raFileDetailsId);
            if (!optionalRAFileDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "raFileDetailsId " + raFileDetailsId + " not found");
            }
            List<RASheetProgressInfo> raSheetProgressInfoList = new ArrayList<>();
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.getRASheetDetailsList(raFileDetails.getId(), dataTypeList);
            for (RASheetDetails raSheetDetails : raSheetDetailsList) {
                if (!isDataSheet(raSheetDetails.getType()) || raSheetDetails.getRosterRecordCount() == null
                        || raSheetDetails.getRosterRecordCount() == 0) {
                    continue;
                }
                raSheetProgressInfoList.add(raFileStatsService.getRASheetProgressInfo(raFileDetails, raSheetDetails));
            }
            return new ResponseEntity<>(raSheetProgressInfoList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRosterFileProgressInfoList raFileDetailsId {}", raFileDetailsId);
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
    public ResponseEntity<CollectionResponse<InCompatibleRosterDetails>> getNonCompatibleFileList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                    @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                    @RequestParam(defaultValue = "") String market,
                                                                                    @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                                    @RequestParam(defaultValue = "") String fileName,
                                                                                    @RequestParam(defaultValue = "") String plmTicketId,
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
            ListResponse<RAFileDetailsWithSheets> raFileDetailsWithSheetsListResponse = raFileDetailsService.getRAFileDetailsWithSheetsList(fileName, plmTicketId,
                    market, lineOfBusiness, startTime, endTime, statusCodes, limit, offset, true, 0, false);
            //TODO
            List<InCompatibleRosterDetails> inCompatibleRosterDetails = new ArrayList<>();

            Map<Long, RAFileDetailsLob> raFileDetailsLobMap = raFileStatsService.getRAFileDetailsLobMap(raFileDetailsWithSheetsListResponse.getItems()
                    .stream().map(p -> p.getRaFileDetails().getId()).collect(Collectors.toList()));
            Map<Long, List<RARTFileAltIds>> rartFileAltIdsListMap = raFileStatsService.getRARTFileAltIdsListMap(raFileDetailsWithSheetsListResponse.
                    getItems().stream().map(p -> p.getRaFileDetails().getId()).collect(Collectors.toList()));
            for (RAFileDetailsWithSheets raFileDetailsWithSheets : raFileDetailsWithSheetsListResponse.getItems()) {
                RAFileDetails raFileDetails = raFileDetailsWithSheets.getRaFileDetails();
                Long raFileDetailsId = raFileDetails.getId();
                List<RAFileErrorCodeDetails> raFileErrorCodeDetailsList = raFileErrorCodeDetailRepository.findByRAFileDetailsId(raFileDetailsWithSheets.getRaFileDetails().getId());
                //TODO need to fix it
                List<String> fileErrorCodes = raFileErrorCodeDetailsList.stream().map(p -> p.getErrorCode()).distinct().collect(Collectors.toList());
                List<String> sheetErrorCodes = new ArrayList<>();
                Integer rosterRecordCount = 0;
                for (RASheetDetails raSheetDetails : raFileDetailsWithSheets.getRaSheetDetailsList()) {
                    List<RASheetErrorCodeDetails> raSheetErrorCodeDetailsList = raSheetErrorCodeDetailRepository
                            .findByRASheetDetailsId(raSheetDetails.getId());
                    sheetErrorCodes.addAll(raSheetErrorCodeDetailsList.stream().map(RASheetErrorCodeDetails::getErrorCode).collect(Collectors.toList()));
                    rosterRecordCount += raSheetDetails.getRosterRecordCount();
                }
                sheetErrorCodes = sheetErrorCodes.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
                RaErrorCodeDetailsService.ErrorCodesAndDescription errorCodesAndDescription = raErrorCodeDetailsService.getErrorString(fileErrorCodes, sheetErrorCodes);
                String lob = raFileDetailsLobMap.containsKey(raFileDetailsId) ? raFileDetailsLobMap.get(raFileDetailsId).getLob() : "-";
                List<RARTFileAltIds> rartFileAltIdsList = rartFileAltIdsListMap.containsKey(raFileDetailsId) ? rartFileAltIdsListMap
                        .get(raFileDetailsId).stream().filter(p -> p.getAltIdType().equals(AltIdType.RO_ID.name())).collect(Collectors.toList()) : new ArrayList<>();
                String filePlmTicketId = rartFileAltIdsList.size() > 0 ? rartFileAltIdsList.get(0).getAltId() : "-";
                long rosterReceivedTime = getRosterReceivedTime(raFileDetails);
                InCompatibleRosterDetails details = new InCompatibleRosterDetails(raFileDetailsId, raFileDetails.getOriginalFileName(), rosterReceivedTime,
                        rosterRecordCount, errorCodesAndDescription.errorDescription,
                        String.join(", ", errorCodesAndDescription.errorCodes), lob, raFileDetails.getMarket(), filePlmTicketId);
                inCompatibleRosterDetails.add(details);
            }
            CollectionResponse<InCompatibleRosterDetails> collectionResponse = new CollectionResponse<InCompatibleRosterDetails>(pageNo, pageSize, inCompatibleRosterDetails, 1000L);
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, fileName, startTime, endTime);
            throw ex;
        }
    }
}
package com.hilabs.rostertracker.controller;

import com.hilabs.roster.dto.AltIdType;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.util.RAStatusEntity;
import com.hilabs.rostertracker.dto.RosterSheetColumnMappingInfo;
import com.hilabs.rostertracker.dto.RosterSheetDetails;
import com.hilabs.rostertracker.dto.SheetDetails;
import com.hilabs.rostertracker.model.*;
import com.hilabs.rostertracker.service.*;
import com.hilabs.rostertracker.utils.LimitAndOffset;
import com.hilabs.rostertracker.utils.Utils;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static com.hilabs.rostertracker.service.RAFileDetailsService.getStatusCodes;

@RestController
@RequestMapping("/api/v1/config-ui")
@Log4j2
@CrossOrigin(origins = "*")
public class ConfigUIController {
    @Autowired
    RAFileDetailsService raFileDetailsService;

    @Autowired
    RASheetDetailsService raSheetDetailsService;

    @Autowired
    RAFileStatsService raFileStatsService;
    @Autowired
    private RARCRosterISFMapService raRcRosterISFMapService;

    @Autowired
    private DummyDataService dummyDataService;

    @Autowired
    private RAStatusService raStatusService;

    @GetMapping("/valid-file-list")
    public ResponseEntity<List<ConfigUiFileData>> getConfigUIValidFileList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                           @RequestParam(defaultValue = "100") Integer pageSize,
                                                                           @RequestParam(defaultValue = "") String market,
                                                                           @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                           @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                           @RequestParam(defaultValue = "-1") long startTime,
                                                                           @RequestParam(defaultValue = "-1") long endTime) {
        try {
            List<Integer> statusCodes = getStatusCodes(RosterFilterType.CONFIGURATOR);
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = Utils.getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            //TODO demo
            List<RAFileDetails> raFileDetailsList = raFileDetailsService.getRAFileDetailsList(raFileDetailsId, market,
                    lineOfBusiness, startTime, endTime, statusCodes, limit, offset);
            List<RASheetDetails> raSheetDetailsList = raSheetDetailsService.findRASheetDetailsListForFileIdsList(raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList()), true);
            Map<Long, List<RASheetDetails>> raSheetDetailsListMap = raFileStatsService.getRASheetDetailsListMap(raFileDetailsList, raSheetDetailsList);
            Map<Long, RAFileDetailsLob> raFileDetailsLobMap = raFileStatsService.getRAFileDetailsLobMap(raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList()));
            Map<Long, List<RARTFileAltIds>> rartFileAltIdsListMap = raFileStatsService.getRARTFileAltIdsListMap(raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList()));
            List<ConfigUiFileData> configUiFileDataList = new ArrayList<>();
            for (RAFileDetails raFileDetails : raFileDetailsList) {
                if (!raSheetDetailsListMap.containsKey(raFileDetails.getId())
                        || raSheetDetailsListMap.get(raFileDetails.getId()).size() == 0) {
                    continue;
                }
                String status = raStatusService.getDisplayStatus(raFileDetails.getStatusCode());
                Optional<RAStatusEntity> optionalRAStatusEntity = RAStatusEntity.getRAFileStatusEntity(raFileDetails.getStatusCode());
                boolean isManualActionReq = (raFileDetails.getManualActionRequired() != null && raFileDetails.getManualActionRequired() == 1);
                String lob = raFileDetailsLobMap.containsKey(raFileDetails.getId()) ? raFileDetailsLobMap.get(raFileDetails.getId()).getLob() : "-";
                List<RARTFileAltIds> rartFileAltIdsList = rartFileAltIdsListMap.containsKey(raFileDetails.getId()) ? rartFileAltIdsListMap
                        .get(raFileDetails.getId()).stream().filter(p -> p.getAltIdType().equals(AltIdType.RO_ID.name())).collect(Collectors.toList()) : new ArrayList<>();
                String plmTicketId = rartFileAltIdsList.size() > 0 ? rartFileAltIdsList.get(0).getAltId() : "-";
                configUiFileDataList.add(new ConfigUiFileData(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                        raFileDetails.getCreatedDate().getTime(), status, optionalRAStatusEntity.map(RAStatusEntity::getStage).orElse(null),
                        lob, raFileDetails.getMarket(), plmTicketId,
                        isManualActionReq));
            }
            return new ResponseEntity<>(configUiFileDataList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }

    //TODO demo remove api
    @GetMapping("/sheet-details")
    public ResponseEntity<List<SheetDetails>> getSheetDetails(@RequestParam(defaultValue = "raFileDetailsId") Long raFileDetailsId) {
        List<SheetDetails> sheetDetailsList = raSheetDetailsService.getAllSheetDetailsWithColumnMappingList(raFileDetailsId);
        //TODO demo
        return ResponseEntity.ok(sheetDetailsList);
    }

    @GetMapping("/roster-sheet-details")
    public ResponseEntity<RosterSheetDetails> getRosterSheetDetails(@RequestParam(defaultValue = "raFileDetailsId") Long raFileDetailsId) {
        List<SheetDetails> sheetDetailsList = raSheetDetailsService.getAllSheetDetailsWithColumnMappingList(raFileDetailsId);
        Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findRAFileDetailsById(raFileDetailsId);
        if (!optionalRAFileDetails.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "raFileDetailsId " + raFileDetailsId);
        }
        RAFileDetails raFileDetails = optionalRAFileDetails.get();
        Optional<RAFileDetailsLob> optionalRAFileDetailsLob = raFileStatsService.getRAFileDetailsLob(raFileDetailsId);
        String lob = optionalRAFileDetailsLob.isPresent() ? optionalRAFileDetailsLob.get().getLob() : "-";
        List<RARTFileAltIds> rartFileAltIdsList = raFileStatsService.getRARTFileAltIdsList(raFileDetailsId)
                .stream().filter(p -> p.getAltIdType().equals(AltIdType.RO_ID.name())).collect(Collectors.toList());
        String plmTicketId = rartFileAltIdsList.size() > 0 ? rartFileAltIdsList.get(0).getAltId() : "-";
        //TODO demo
        Long lastApprovedTime = raFileDetails.getLastApprovedDate() != null ? raFileDetails.getLastApprovedDate().getTime() : -1;
        Long lastSavedTime = raFileDetails.getLastSavedDate() != null ? raFileDetails.getLastSavedDate().getTime() : -1;
        String lastSavedBy = raFileDetails.getLastSavedBy();
        String lastApprovedBy = raFileDetails.getLastApprovedBy();
        return ResponseEntity.ok(new RosterSheetDetails(raFileDetailsId, sheetDetailsList, lob, raFileDetails.getMarket(),
                plmTicketId, lastSavedTime, lastSavedBy, lastApprovedTime, lastApprovedBy, raFileDetails.getVersion()));
    }

    @GetMapping("/sheet-column-mapping")
    public ResponseEntity<RosterSheetColumnMappingInfo> getSheetColumnMapping(@RequestParam(defaultValue = "raSheetDetailsId") Long raSheetDetailsId) {
        RosterSheetColumnMappingInfo rosterSheetColumnMappingInfo = raRcRosterISFMapService.getRosterSheetColumnMappingInfoForSheetId(raSheetDetailsId);
        return ResponseEntity.ok(rosterSheetColumnMappingInfo);
    }

    @PostMapping("/save-column-mapping")
    public ResponseEntity<Map<String, String>> saveColumnMapping(@RequestBody UpdateColumnMappingRequest updateColumnMappingRequest) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            raRcRosterISFMapService.saveColumnMappingWithLock(updateColumnMappingRequest, false, username);
            //TODO return better response
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping updateColumnMappingRequest {}", updateColumnMappingRequest);
            throw ex;
        }
    }

    @PostMapping("/approve-column-mapping")
    public ResponseEntity<Map<String, String>> approveColumnMapping(@RequestBody UpdateColumnMappingRequest updateColumnMappingRequest) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            raRcRosterISFMapService.saveColumnMappingWithLock(updateColumnMappingRequest, true, username);
            //TODO return better response
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping message {} stackTrace {}", ex.getMessage(),
                    ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }
}
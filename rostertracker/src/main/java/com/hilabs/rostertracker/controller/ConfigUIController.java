package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.util.RAStatusEntity;
import com.hilabs.rostertracker.dto.CollectionResponse;
import com.hilabs.rostertracker.dto.RosterSheetColumnMappingInfo;
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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CollectionResponse<ConfigUiFileData>> getConfigUIValidFileList(@RequestParam(defaultValue = "1") Integer pageNo,
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
            List<ConfigUiFileData> configUiFileDataList = new ArrayList<>();
            for (RAFileDetails raFileDetails : raFileDetailsList) {
                if (!raSheetDetailsListMap.containsKey(raFileDetails.getId())
                        || raSheetDetailsListMap.get(raFileDetails.getId()).size() == 0) {
                    continue;
                }
                String status = raStatusService.getDisplayStatus(raFileDetails.getStatusCode());
                Optional<RAStatusEntity> optionalRAStatusEntity = RAStatusEntity.getRAFileStatusEntity(raFileDetails.getStatusCode());
                boolean isManualActionReq = (raFileDetails.getManualActionRequired() != null && raFileDetails.getManualActionRequired() == 1);
                configUiFileDataList.add(new ConfigUiFileData(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                        raFileDetails.getCreatedDate().getTime(), status, optionalRAStatusEntity.map(RAStatusEntity::getStage).orElse(null),
                        //TODO demo
                        isManualActionReq));
            }
            CollectionResponse collectionResponse = new CollectionResponse(pageNo, pageSize, configUiFileDataList, 1000L);
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/sheet-details")
    public ResponseEntity<CollectionResponse<SheetDetails>> getSheetDetails(@RequestParam(defaultValue = "raFileDetailsId") Long raFileDetailsId) {
        List<SheetDetails> sheetDetailsList = raSheetDetailsService.getAllSheetDetailsWithColumnMappingList(raFileDetailsId);
        //TODO demo
        return ResponseEntity.ok(new CollectionResponse(1, 100, sheetDetailsList, 1000L));
    }

    @GetMapping("/sheet-column-mapping")
    public ResponseEntity<RosterSheetColumnMappingInfo> getSheetColumnMapping(@RequestParam(defaultValue = "raSheetDetailsId") Long raSheetDetailsId) {
        RosterSheetColumnMappingInfo rosterSheetColumnMappingInfo = raRcRosterISFMapService.getRosterSheetColumnMappingInfoForSheetId(raSheetDetailsId);
        return ResponseEntity.ok(rosterSheetColumnMappingInfo);
    }

    @PostMapping("/save-column-mapping")
    public ResponseEntity<Map<String, String>> saveColumnMapping(@RequestBody UpdateColumnMappingRequest updateColumnMappingRequest) {
        try {
            List<UpdateColumnMappingSheetData> sheetDataList = updateColumnMappingRequest.getSheetDataList();
            for (UpdateColumnMappingSheetData sheetData : sheetDataList) {
                Long raSheetDetailsId = sheetData.getRaSheetDetailsId();
                Map<String, String> data = sheetData.getData();
                //TODO get only whatever is needed
                List<RARCRosterISFMap> rarcRosterISFMapList = raRcRosterISFMapService
                        .getActiveRARCRosterISFMapListForSheetId(raSheetDetailsId);
                raRcRosterISFMapService.updateSheetMapping(rarcRosterISFMapList, data, raSheetDetailsId);
            }
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
            Long raFileDetailsId = updateColumnMappingRequest.getRaFileDetailsId();
            saveColumnMapping(updateColumnMappingRequest);
            raFileDetailsService.updateManualActionRequiredInRAFileDetails(raFileDetailsId, 0);
            //TODO return better response
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping message {} stackTrace {}", ex.getMessage(),
                    ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }
}
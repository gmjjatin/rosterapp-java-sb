package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.util.RAStatusEntity;
import com.hilabs.rostertracker.dto.RosterSheetColumnMappingInfo;
import com.hilabs.rostertracker.dto.SheetDetails;
import com.hilabs.rostertracker.model.ConfigUiFileData;
import com.hilabs.rostertracker.model.RAFileDetailsListAndSheetList;
import com.hilabs.rostertracker.model.UpdateColumnMappingRequest;
import com.hilabs.rostertracker.model.UpdateColumnMappingSheetData;
import com.hilabs.rostertracker.service.RAFileDetailsService;
import com.hilabs.rostertracker.service.RAFileStatsService;
import com.hilabs.rostertracker.service.RARCRosterISFMapService;
import com.hilabs.rostertracker.service.RAStatusService;
import com.hilabs.rostertracker.utils.LimitAndOffset;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.hilabs.rostertracker.service.RAFileDetailsService.getStatusCodes;

@RestController
@RequestMapping("/api/v1/config-ui")
@Log4j2
@CrossOrigin(origins = "*")
public class ConfigUIController {
    @Autowired
    RAFileDetailsService raFileDetailsService;
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
            List<Integer> statusCodes = getStatusCodes("config");
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            int offset = limitAndOffset.getOffset();
            Utils.StartAndEndTime startAndEndTime = Utils.getAdjustedStartAndEndTime(startTime, endTime);
            startTime = startAndEndTime.startTime;
            endTime = startAndEndTime.endTime;
            //TODO demo
            RAFileDetailsListAndSheetList raFileDetailsListAndSheetList = raFileDetailsService.getRosterSourceListAndFilesList(raFileDetailsId, market,
                    lineOfBusiness, startTime, endTime, limit, offset, statusCodes);
            Map<Long, List<RASheetDetails>> raSheetDetailsListMap = raFileDetailsListAndSheetList.getRASheetDetailsListMap();
            List<ConfigUiFileData> configUiFileDataList = new ArrayList<>();
            for (RAFileDetails raFileDetails : raFileDetailsListAndSheetList.getRaFileDetailsList()) {
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
                        !isManualActionReq));
            }
            return new ResponseEntity<>(configUiFileDataList, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }

    @GetMapping("/sheet-details")
    public ResponseEntity<List<SheetDetails>> getSheetDetails(@RequestParam(defaultValue = "raFileDetailsId") Long raFileDetailsId) {
        List<SheetDetails> sheetDetailsList = raFileDetailsService.getRASheetDetailsList(raFileDetailsId);
        //TODO demo
        return ResponseEntity.ok(sheetDetailsList);
    }

    @GetMapping("/sheet-column-mapping")
    public ResponseEntity<RosterSheetColumnMappingInfo> getSheetColumnMapping(@RequestParam(defaultValue = "raSheetDetailsId") Long raSheetDetailsId) {
        //TODO fix the API
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
            //TODO
            Map<String, String> map = new HashMap<>();
            map.put("SUCCESSFUL", "SUCCESSFUL");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping updateColumnMappingRequest {}", updateColumnMappingRequest);
            throw ex;
        }
    }

    @PostMapping("/approve-column-mapping")
    public ResponseEntity<Map<String, String>> approveColumnMapping(@RequestBody UpdateColumnMappingRequest updateColumnMappingRequest) {
        try {
            //TODO yet to be implemented
            Map<String, String> map = new HashMap<>();
            map.put("SUCCESSFUL", "SUCCESSFUL");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping updateColumnMappingRequest {}", updateColumnMappingRequest);
            throw ex;
        }
    }
}
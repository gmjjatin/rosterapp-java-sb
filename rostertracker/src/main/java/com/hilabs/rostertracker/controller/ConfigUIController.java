package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.rostertracker.dto.RAFileAndStats;
import com.hilabs.rostertracker.dto.RosterSheetColumnMappingInfo;
import com.hilabs.rostertracker.dto.SheetDetails;
import com.hilabs.rostertracker.model.ConfigUiFileData;
import com.hilabs.rostertracker.model.RAFileDetailsListAndSheetList;
import com.hilabs.rostertracker.model.UpdateColumnMappingRequest;
import com.hilabs.rostertracker.model.UpdateColumnMappingSheetData;
import com.hilabs.rostertracker.service.RAFileDetailsService;
import com.hilabs.rostertracker.service.RAFileStatsService;
import com.hilabs.rostertracker.service.RARCRosterISFMapService;
import com.hilabs.rostertracker.utils.LimitAndOffset;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.hilabs.roster.util.RosterStageUtils.getNonFailedFileStatusCodes;

@RestController
@RequestMapping("/api/v1/config-ui")
@Log4j2
public class ConfigUIController {
    @Autowired
    RAFileDetailsService raFileDetailsService;
    @Autowired
    RAFileStatsService raFileStatsService;
    @Autowired
    private RARCRosterISFMapService raRcRosterISFMapService;

    @Autowired
    private DummyDataService dummyDataService;

    @GetMapping("/valid-file-list")
    public ResponseEntity<List<ConfigUiFileData>> getConfigUIValidFileList(@RequestParam(defaultValue = "1") Integer pageNo,
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
            //TODO
            RAFileDetailsListAndSheetList raFileDetailsListAndSheetList = raFileDetailsService
                    .getRosterSourceListAndFilesList(raFileDetailsId, market, lineOfBusiness,
                            startTime, endTime, limit, offset, getNonFailedFileStatusCodes());
            List<RAFileAndStats> raFileAndStatsList = raFileStatsService.getRAFileAndStats(raFileDetailsListAndSheetList);
            return new ResponseEntity<>(dummyDataService.getConfigUIValidFileList(), HttpStatus.OK);
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
        return ResponseEntity.ok(dummyDataService.getSheetDetails(raFileDetailsId));
    }

    @GetMapping("/sheet-column-mapping")
    public ResponseEntity<RosterSheetColumnMappingInfo> getSheetColumnMapping(@RequestParam(defaultValue = "raSheetDetailsId") Long raSheetDetailsId) {
        //TODO fix the API
        return ResponseEntity.ok(dummyDataService.getSheetColumnMapping(raSheetDetailsId));
    }

    @PostMapping("/save-column-mapping")
    public ResponseEntity<String> saveColumnMapping(@RequestBody UpdateColumnMappingRequest updateColumnMappingRequest) {
        try {
            List<UpdateColumnMappingSheetData> sheetDataList = updateColumnMappingRequest.getSheetDataList();
            for (UpdateColumnMappingSheetData sheetData : sheetDataList) {
                Long raSheetDetailsId = sheetData.getRaSheetDetailsId();
                Map<String, String> data = sheetData.getData();
                //TODO get only whatever is needed
                List<RARCRosterISFMap> rarcRosterISFMapList = raRcRosterISFMapService
                        .getRARCRosterISFMapListForSheetId(raSheetDetailsId);
                raRcRosterISFMapService.updateSheetMapping(rarcRosterISFMapList, data, raSheetDetailsId);
            }
            //TODO
            return new ResponseEntity<>("SUCCESSFUL", HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping updateColumnMappingRequest {}", updateColumnMappingRequest);
            throw ex;
        }
    }

    @PostMapping("/approve-column-mapping")
    public ResponseEntity<String> approveColumnMapping(@RequestBody UpdateColumnMappingRequest updateColumnMappingRequest) {
        try {
            //TODO yet to be implemented
            return new ResponseEntity<>("SUCCESSFUL", HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping updateColumnMappingRequest {}", updateColumnMappingRequest);
            throw ex;
        }
    }
}
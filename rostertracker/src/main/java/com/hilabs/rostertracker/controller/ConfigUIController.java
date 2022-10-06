package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RARCRosterISFMap;
import com.hilabs.rostertracker.controller.model.UpdateColumnMappingRequest;
import com.hilabs.rostertracker.controller.model.UpdateColumnMappingSheetData;
import com.hilabs.rostertracker.dto.RAFileAndStats;
import com.hilabs.rostertracker.service.RARCRosterISFMapService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/config-ui")
@Log4j2
public class ConfigUIController {
    @Autowired
    private RARCRosterISFMapService raRcRosterISFMapService;

    @GetMapping("/file-list")
    public ResponseEntity<List<RAFileAndStats>> getRAProvAndStatsList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(defaultValue = "100") Integer pageSize,
                                                                      @RequestParam(defaultValue = "") String market,
                                                                      @RequestParam(defaultValue = "") String lineOfBusiness,
                                                                      @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                      @RequestParam(defaultValue = "-1") long startTime,
                                                                      @RequestParam(defaultValue = "-1") long endTime) {
        try {
            //TODO
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} market {} lineOfBusiness {} raFileDetailsId {} startTime {} endTime {}",
                    pageNo, pageSize, market, lineOfBusiness, raFileDetailsId, startTime, endTime);
            throw ex;
        }
    }

    @PostMapping("/updateColumnMapping")
    public ResponseEntity<String> updateColumnMapping(@RequestBody UpdateColumnMappingRequest updateColumnMappingRequest) {
        try {
            Long raFileDetailsId = updateColumnMappingRequest.getRaFileDetailsId();
            List<UpdateColumnMappingSheetData> sheetDataList = updateColumnMappingRequest.getSheetDataList();
            for (UpdateColumnMappingSheetData sheetData : sheetDataList) {
                Long raSheetDetailsId = sheetData.getRaSheetDetailsId();
                Map<String, String> data = sheetData.getData();
                //TODO get only whatever is needed
                List<RARCRosterISFMap> rarcRosterISFMapList = raRcRosterISFMapService
                        .getRARCRosterISFMapListForSheetId(raSheetDetailsId);
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateColumnMapping updateColumnMappingRequest {}", updateColumnMappingRequest);
            throw ex;
        }
    }
}
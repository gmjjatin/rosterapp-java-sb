package com.hilabs.rostertracker.controller;

import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.rostertracker.dto.SheetDetails;
import com.hilabs.rostertracker.model.ConfigUiFileData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DummyDataService {
//    public RosterSheetColumnMappingInfo getSheetColumnMapping(Long raSheetDetailsId) {
//        List<RosterColumnMappingData> data = new ArrayList<>();
//        data.add(new RosterColumnMappingData("Column Name 1", Arrays.asList("Option 1", "Option 2", "Option 3")));
//        data.add(new RosterColumnMappingData("Column Name 2", Arrays.asList("Option 1", "Option 2", "Option 3")));
//        data.add(new RosterColumnMappingData("Column Name 3", Arrays.asList("Option 1", "Option 2", "Option 3")));
//        data.add(new RosterColumnMappingData("Column Name 4", Arrays.asList("Option 1", "Option 2", "Option 3")));
//        return new RosterSheetColumnMappingInfo(raSheetDetailsId, data);
//    }

    public List<SheetDetails> getSheetDetails(Long raFileDetailsId) {
        List<SheetDetails> sheetDetailsList = new ArrayList<>();
        sheetDetailsList.add(new SheetDetails(1L, "Adds", "Automated", "AUTOMATED", true));
        sheetDetailsList.add(new SheetDetails(2L, "Changes", "Automated", "AUTOMATED", true));
        sheetDetailsList.add(new SheetDetails(3L, "Terms", "MANUAL PROCESSING", "AUTOMATED", true));
        return sheetDetailsList;
    }

    public List<ConfigUiFileData> getConfigUIValidFileList() {
        return Arrays.asList(new ConfigUiFileData(1L, "Sample File", System.currentTimeMillis(),
                        "Auto Mapped", RosterSheetProcessStage.AUTO_MAPPED, true),
                new ConfigUiFileData(2L, "Sample File 2",
                        System.currentTimeMillis(), "Dart File Generated", RosterSheetProcessStage.CONVERTED_DART, false));
    }
}

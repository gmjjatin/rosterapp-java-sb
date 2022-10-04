package com.hilabs.rostertracker.model;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RAFileDetailsListAndSheetList {
    private List<RAFileDetails> raFileDetailsList;
    private List<RASheetDetails> raSheetDetailsList;

    public RAFileDetailsListAndSheetList(List<RAFileDetails> raFileDetailsList, List<RASheetDetails> raSheetDetailsList) {
        this.raFileDetailsList = raFileDetailsList;
        this.raSheetDetailsList = raSheetDetailsList;
    }

    public Map<Long, List<RASheetDetails>> getRASheetDetailsListMap() {
        Map<Long, List<RASheetDetails>> raSheetDetailsListMap = new HashMap<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            raSheetDetailsListMap.putIfAbsent(raSheetDetails.getRaFileDetailsId(), new ArrayList<>());
            raSheetDetailsListMap.get(raSheetDetails.getRaFileDetailsId()).add(raSheetDetails);
        }
        return raSheetDetailsListMap;
    }

    public Map<Long, RAFileDetails> getRAFileDetailsMap() {
        Map<Long, RAFileDetails> raFileDetailsMap = new HashMap<>();
        for (RAFileDetails raFileDetails : raFileDetailsList) {
            raFileDetailsMap.put(raFileDetails.getId(), raFileDetails);
        }
        return raFileDetailsMap;
    }

}

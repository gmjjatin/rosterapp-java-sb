package com.hilabs.rostertracker.utils;

import com.hilabs.rostertracker.dto.RAFileAndErrorStats;
import com.hilabs.rostertracker.dto.RAFileAndStats;
import com.hilabs.rostertracker.dto.RASheetAndErrorStats;
import com.hilabs.rostertracker.dto.RASheetAndStats;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RAProvDetails;
import com.hilabs.roster.entity.RASheetDetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hilabs.roster.model.RosterFileProcessStatus.SUCCEEDED;
import static com.hilabs.roster.util.Constants.*;

public class RosterUtils {
    public static List<RAFileDetails> removeDuplicateRAProvList(List<RAFileDetails> raProvDetailsList) {
        Set<Long> ids = new HashSet<>();
        List<RAFileDetails> uniqueRAProvDetailsList = new ArrayList<>();
        for (RAFileDetails raProvDetails : raProvDetailsList) {
            if (ids.contains(raProvDetails.getId())) {
                continue;
            }
            ids.add(raProvDetails.getId());
            uniqueRAProvDetailsList.add(raProvDetails);
        }
        return uniqueRAProvDetailsList;
    }

    //TODO confirm??
    public static int computeFalloutRecordCount(RASheetDetails raSheetDetails) {
        return raSheetDetails.getRosterRecordCount() - raSheetDetails.getTargetSuccessfulRecordCount() - raSheetDetails.getManualReviewRecordCount();
    }
}

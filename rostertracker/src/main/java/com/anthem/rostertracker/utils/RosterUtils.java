package com.anthem.rostertracker.utils;

import com.anthem.rostertracker.dto.RAFileAndErrorStats;
import com.anthem.rostertracker.dto.RAFileAndStats;
import com.anthem.rostertracker.dto.RASheetAndErrorStats;
import com.anthem.rostertracker.dto.RASheetAndStats;
import com.anthem.rostertracker.entity.RAFileDetails;
import com.anthem.rostertracker.entity.RAProvDetails;
import com.anthem.rostertracker.entity.RASheetDetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RosterUtils {
    public static List<RAProvDetails> removeDuplicateRAProvList(List<RAProvDetails> raProvDetailsList) {
        Set<Long> ids = new HashSet<>();
        List<RAProvDetails> uniqueRAProvDetailsList = new ArrayList<>();
        for (RAProvDetails raProvDetails : raProvDetailsList) {
            if (ids.contains(raProvDetails.getId())) {
                continue;
            }
            ids.add(raProvDetails.getId());
            uniqueRAProvDetailsList.add(raProvDetails);
        }
        return uniqueRAProvDetailsList;
    }

    public static RASheetAndErrorStats getRASheetAndErrorStats(RASheetDetails raSheetDetails, boolean isSpsLoadComplete) {
        RASheetAndStats raSheetAndStats = getRASheetAndStats(raSheetDetails, isSpsLoadComplete);
        RASheetAndErrorStats raSheetAndErrorStats = new RASheetAndErrorStats(raSheetDetails.getId(), raSheetDetails.getName(), raSheetAndStats);
        raSheetAndErrorStats.setSpsLoadTransactionCount(raSheetDetails.getSpsLoadTransactionCount());
        raSheetAndErrorStats.setSpsLoadSuccessTransactionCount(raSheetDetails.getSpsLoadSuccessTransactionCount());
        raSheetAndErrorStats.setSpsLoadFailedTransactionCount(raSheetDetails.getSpsLoadTransactionCount() - raSheetDetails.getSpsLoadSuccessTransactionCount());
        double percent = raSheetDetails.getSpsLoadTransactionCount() > 0 ? (raSheetDetails.getSpsLoadSuccessTransactionCount() * 100.0 / raSheetDetails.getSpsLoadTransactionCount()) : 0;
        raSheetAndErrorStats.setSpsLoadSuccessTransactionPercent(percent);
        return raSheetAndErrorStats;
    }


    public static RASheetAndStats getRASheetAndStats(RASheetDetails raSheetDetails, boolean isSpsLoadComplete) {
        RASheetAndStats raSheetAndStats = new RASheetAndStats(raSheetDetails.getId(), raSheetDetails.getName(), isSpsLoadComplete);
        raSheetAndStats.setRosterRecordCount(raSheetDetails.getRosterRecordCount());
        raSheetAndStats.setSuccessfulRecordCount(raSheetDetails.getSuccessfulRecordCount());
        raSheetAndStats.setFalloutRecordCount(computeFalloutRecordCount(raSheetDetails));
        raSheetAndStats.setManualReviewRecordCount(raSheetDetails.getManualReviewRecordCount());
        return raSheetAndStats;
    }

    //TODO confirm??
    public static int computeFalloutRecordCount(RASheetDetails raSheetDetails) {
        return raSheetDetails.getRosterRecordCount() - raSheetDetails.getSuccessfulRecordCount() - raSheetDetails.getManualReviewRecordCount();
    }

    public static RAFileAndErrorStats getRAFileAndErrorStatsFromSheetDetailsList(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        RAFileAndErrorStats raFileAndErrorStats = new RAFileAndErrorStats(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : -1);
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            RASheetAndErrorStats raSheetAndErrorStats = getRASheetAndErrorStats(raSheetDetails, isSpsLoadComplete(raSheetDetails));
            raFileAndErrorStats.addSheetDetails(raSheetAndErrorStats);
        }
        return raFileAndErrorStats;
    }

    public static RAFileAndStats getRAFileAndStatsFromSheetDetailsList(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        RAFileAndStats raFileAndStats = new RAFileAndStats(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : -1);
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            RASheetAndStats raSheetAndStats = getRASheetAndStats(raSheetDetails, isSpsLoadComplete(raSheetDetails));
            raFileAndStats.addSheetDetails(raSheetAndStats);
        }
        return raFileAndStats;
    }

    //TODO demo
    public static boolean isSpsLoadComplete(RASheetDetails raSheetDetails) {
        //TODO manikanta
        return raSheetDetails.getId() % 2 == 0;
    }
}

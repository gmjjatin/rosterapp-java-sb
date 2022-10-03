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

    public static RASheetAndErrorStats getRASheetAndErrorStats(RASheetDetails raSheetDetails, String status) {
        RASheetAndStats raSheetAndStats = getRASheetAndStats(raSheetDetails, status);
        RASheetAndErrorStats raSheetAndErrorStats = new RASheetAndErrorStats(raSheetDetails.getId(),
                raSheetDetails.getTabName(), raSheetAndStats);
        raSheetAndErrorStats.setSpsLoadTransactionCount(raSheetDetails.getTargetLoadTransactionCount());
        raSheetAndErrorStats.setSpsLoadSuccessTransactionCount(raSheetDetails.getTargetLoadSuccessTransactionCount());
        raSheetAndErrorStats.setSpsLoadFailedTransactionCount(raSheetDetails.getTargetLoadTransactionCount() - raSheetDetails.getTargetLoadSuccessTransactionCount());
        double percent = raSheetDetails.getTargetLoadTransactionCount() > 0 ? (raSheetDetails.getTargetLoadSuccessTransactionCount() * 100.0 / raSheetDetails.getTargetLoadTransactionCount()) : 0;
        raSheetAndErrorStats.setSpsLoadSuccessTransactionPercent(percent);
        return raSheetAndErrorStats;
    }


    public static RASheetAndStats getRASheetAndStats(RASheetDetails raSheetDetails, String status) {
        RASheetAndStats raSheetAndStats = new RASheetAndStats(raSheetDetails.getId(),
                raSheetDetails.getTabName(), status);
        raSheetAndStats.setRosterRecordCount(raSheetDetails.getRosterRecordCount());
        raSheetAndStats.setSuccessfulRecordCount(raSheetDetails.getTargetSuccessfulRecordCount());
        raSheetAndStats.setFalloutRecordCount(computeFalloutRecordCount(raSheetDetails));
        raSheetAndStats.setManualReviewRecordCount(raSheetDetails.getManualReviewRecordCount());
        return raSheetAndStats;
    }

    //TODO confirm??
    public static int computeFalloutRecordCount(RASheetDetails raSheetDetails) {
        return raSheetDetails.getRosterRecordCount() - raSheetDetails.getTargetSuccessfulRecordCount() - raSheetDetails.getManualReviewRecordCount();
    }

    public static RAFileAndErrorStats getRAFileAndErrorStatsFromSheetDetailsList(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        RAFileAndErrorStats raFileAndErrorStats = new RAFileAndErrorStats(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : -1);
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            RASheetAndErrorStats raSheetAndErrorStats = getRASheetAndErrorStats(raSheetDetails,
                    getStatus(raSheetDetails));
            raFileAndErrorStats.addSheetDetails(raSheetAndErrorStats);
        }
        return raFileAndErrorStats;
    }

    public static RAFileAndStats getRAFileAndStatsFromSheetDetailsList(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        RAFileAndStats raFileAndStats = new RAFileAndStats(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : -1, getStatus(raFileDetails));
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            RASheetAndStats raSheetAndStats = getRASheetAndStats(raSheetDetails, getStatus(raSheetDetails));
            raFileAndStats.addSheetDetails(raSheetAndStats);
        }
        return raFileAndStats;
    }


    //TODO demo move to common
    public static String getStatus(RASheetDetails raSheetDetails) {
        return "INGESTION COMPLETE";
    }

    public static String getStatus(RAFileDetails raFileDetails) {
        return "INGESTION COMPLETE";
    }
}

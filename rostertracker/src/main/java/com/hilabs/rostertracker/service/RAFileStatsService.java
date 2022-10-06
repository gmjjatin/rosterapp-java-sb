package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RARTConvProcessingDurationStats;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import com.hilabs.roster.repository.RAConvProcessingDurationStatsRepository;
import com.hilabs.roster.repository.RAStatusCDMasterRepository;
import com.hilabs.rostertracker.dto.RAFileAndErrorStats;
import com.hilabs.rostertracker.dto.RAFileAndStats;
import com.hilabs.rostertracker.dto.RASheetAndErrorStats;
import com.hilabs.rostertracker.dto.RASheetAndStats;
import com.hilabs.rostertracker.model.*;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hilabs.roster.util.Constants.computeTimeTakenInMillis;
import static com.hilabs.roster.util.Constants.getRosterStageState;
import static com.hilabs.rostertracker.utils.RosterUtils.computeFalloutRecordCount;

@Service
@Log4j2
public class RAFileStatsService {
    @Autowired
    private RAConvProcessingDurationStatsRepository raConvProcessingDurationStatsRepository;

    @Autowired
    private RAStatusCDMasterRepository raStatusCDMasterRepository;

    @Autowired
    private RAStatusService raStatusService;

    public List<RAFileAndErrorStats> getRAFileAndErrorStats(RAFileDetailsListAndSheetList raFileDetailsListAndSheetList) {
        List<RAFileDetails> raFileDetailsList = raFileDetailsListAndSheetList.getRaFileDetailsList();
        List<RASheetDetails> raSheetDetailsList = raFileDetailsListAndSheetList.getRaSheetDetailsList();
        Map<Long, List<RASheetDetails>> rosterSheetDetailsListMap = new HashMap<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            rosterSheetDetailsListMap.putIfAbsent(raSheetDetails.getRaFileDetailsId(), new ArrayList<>());
            rosterSheetDetailsListMap.get(raSheetDetails.getRaFileDetailsId()).add(raSheetDetails);
        }
        List<RAFileAndErrorStats> raFileAndErrorStatsList = new ArrayList<>();
        for (RAFileDetails raFileDetails : raFileDetailsList) {
            raFileAndErrorStatsList.add(getRAFileAndErrorStats(raFileDetails, rosterSheetDetailsListMap.getOrDefault(raFileDetails.getId(), new ArrayList<>())));
        }
        return raFileAndErrorStatsList;
    }

    public List<RAFileAndStats> getRAFileAndStats(RAFileDetailsListAndSheetList raFileDetailsListAndSheetList) {
        List<RAFileDetails> raFileDetailsList = raFileDetailsListAndSheetList.getRaFileDetailsList();
        List<RASheetDetails> raSheetDetailsList = raFileDetailsListAndSheetList.getRaSheetDetailsList();
        Map<Long, List<RASheetDetails>> rosterSheetDetailsListMap = new HashMap<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            rosterSheetDetailsListMap.putIfAbsent(raSheetDetails.getRaFileDetailsId(), new ArrayList<>());
            rosterSheetDetailsListMap.get(raSheetDetails.getRaFileDetailsId()).add(raSheetDetails);
        }
        List<RAFileAndStats> raFileAndStatsList = new ArrayList<>();
        for (RAFileDetails raFileDetails : raFileDetailsList) {
            raFileAndStatsList.add(getRAFileAndStats(raFileDetails, rosterSheetDetailsListMap.getOrDefault(raFileDetails.getId(), new ArrayList<>())));
        }
        return raFileAndStatsList;
    }

    public RAFileAndErrorStats getRAFileAndErrorStats(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        return getRAFileAndErrorStatsFromSheetDetailsList(raFileDetails, raSheetDetailsList);
    }

    public RAFileAndStats getRAFileAndStats(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        return getRAFileAndStatsFromSheetDetailsList(raFileDetails, raSheetDetailsList);
    }

    public List<RARTConvProcessingDurationStats> getRosConvProcessingDurationStatsList(long raSheetDetailsId) {
        return raConvProcessingDurationStatsRepository.getRAConvProcessingDurationStatsList(raSheetDetailsId);
    }
    public static RASheetProgressInfo getBaseRosterSheetProgressInfo(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        //TODO received time when created is null
        return new RASheetProgressInfo(raSheetDetails.getId(), raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : 0);
    }

    public RASheetProgressInfo getRASheetProgressInfo(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        RASheetProgressInfo rosterFileProgressInfo = getBaseRosterSheetProgressInfo(raFileDetails, raSheetDetails);
        List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList = getRosConvProcessingDurationStatsList(raSheetDetails.getId());

        RosterStageState autoMappedRosterStageState = getRosterStageState(RosterSheetProcessStage.AUTO_MAPPED, raSheetDetails.getStatusCode());
        if (autoMappedRosterStageState != RosterStageState.NOT_STARTED) {
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.AUTO_MAPPED,
                    autoMappedRosterStageState, computeTimeTakenInMillis(raConvProcessingDurationStatsList, RosterSheetProcessStage.AUTO_MAPPED));
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo,
                    raSheetDetails.getRosterRecordCount(), Utils.MILLIS_IN_HOUR);
            rosterFileProgressInfo.setAutoMapped(new AutoMappedStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState dartRosterStageState = getRosterStageState(RosterSheetProcessStage.CONVERTED_DART, raSheetDetails.getStatusCode());
        if (dartRosterStageState != RosterStageState.NOT_STARTED) {
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.CONVERTED_DART,
                    dartRosterStageState, computeTimeTakenInMillis(raConvProcessingDurationStatsList, RosterSheetProcessStage.CONVERTED_DART));
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo,
                    raSheetDetails.getOutRowCount(), Utils.MILLIS_IN_HOUR);
            rosterFileProgressInfo.setConvertedDart(new ConvertedDartStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState spsLoadRosterStageState = getRosterStageState(RosterSheetProcessStage.SPS_LOAD, raSheetDetails.getStatusCode());
        if (spsLoadRosterStageState != RosterStageState.NOT_STARTED) {
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.SPS_LOAD,
                    spsLoadRosterStageState, computeTimeTakenInMillis(raConvProcessingDurationStatsList, RosterSheetProcessStage.SPS_LOAD));
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo,
                    raSheetDetails.getTargetLoadTransactionCount(), Utils.MILLIS_IN_HOUR);
            rosterFileProgressInfo.setConvertedDart(new ConvertedDartStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        if (spsLoadRosterStageState == RosterStageState.COMPLETED) {
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.REPORT,
                    RosterStageState.COMPLETED, 0);
            rosterFileProgressInfo.setReport(new ReportStageInfo(baseRosterFileProcessStageInfo));
        }
        return rosterFileProgressInfo;
    }

    public RAFileAndErrorStats getRAFileAndErrorStatsFromSheetDetailsList(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        RAFileAndErrorStats raFileAndErrorStats = new RAFileAndErrorStats(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : -1);
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            RASheetAndErrorStats raSheetAndErrorStats = getRASheetAndErrorStats(raSheetDetails,
                    raStatusService.getDisplayStatus(raSheetDetails.getStatusCode()));
            raFileAndErrorStats.addSheetDetails(raSheetAndErrorStats);
        }
        return raFileAndErrorStats;
    }

    public RAFileAndStats getRAFileAndStatsFromSheetDetailsList(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        RAFileAndStats raFileAndStats = new RAFileAndStats(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : -1, raStatusService.getDisplayStatus(raFileDetails.getStatusCode()));
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            RASheetAndStats raSheetAndStats = getRASheetAndStats(raSheetDetails, raStatusService.getDisplayStatus(raSheetDetails.getStatusCode()));
            raFileAndStats.addSheetDetails(raSheetAndStats);
        }
        return raFileAndStats;
    }

    public RASheetAndErrorStats getRASheetAndErrorStats(RASheetDetails raSheetDetails, String status) {
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


    public RASheetAndStats getRASheetAndStats(RASheetDetails raSheetDetails, String status) {
        RASheetAndStats raSheetAndStats = new RASheetAndStats(raSheetDetails.getId(),
                raSheetDetails.getTabName(), status);
        raSheetAndStats.setRosterRecordCount(raSheetDetails.getRosterRecordCount());
        raSheetAndStats.setSuccessfulRecordCount(raSheetDetails.getTargetSuccessfulRecordCount());
        raSheetAndStats.setFalloutRecordCount(computeFalloutRecordCount(raSheetDetails));
        raSheetAndStats.setManualReviewRecordCount(raSheetDetails.getManualReviewRecordCount());
        return raSheetAndStats;
    }
}

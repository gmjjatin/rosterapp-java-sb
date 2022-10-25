package com.hilabs.rostertracker.service;

import com.hilabs.roster.dto.AltIdType;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import com.hilabs.roster.repository.RAConvProcessingDurationStatsRepository;
import com.hilabs.roster.repository.RAFileDetailsLobRepository;
import com.hilabs.roster.repository.RARTFileAltIdsRepository;
import com.hilabs.roster.repository.RAStatusCDMasterRepository;
import com.hilabs.roster.util.ProcessDurationInfo;
import com.hilabs.rostertracker.dto.*;
import com.hilabs.rostertracker.model.*;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hilabs.roster.util.RosterStageUtils.*;
import static com.hilabs.rostertracker.utils.RosterUtils.computeFalloutRecordCount;

@Service
@Log4j2
public class RAFileStatsService {
    @Autowired
    private RAConvProcessingDurationStatsRepository raConvProcessingDurationStatsRepository;

    @Autowired
    private RAStatusCDMasterRepository raStatusCDMasterRepository;

    @Autowired
    private RAFileDetailsLobRepository raFileDetailsLobRepository;

    @Autowired
    private RARTFileAltIdsRepository rartFileAltIdsRepository;

    @Autowired
    private RAStatusService raStatusService;

    public List<RAFileAndErrorStats> getRAFileAndErrorStats(List<RAFileDetails> raFileDetailsList,  List<RASheetDetails> raSheetDetailsList) {
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

    public List<RAFileAndStats> getRAFileAndStats(List<RAFileDetails> raFileDetailsList,  List<RASheetDetails> raSheetDetailsList) {
        Map<Long, List<RASheetDetails>> rosterSheetDetailsListMap = new HashMap<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            rosterSheetDetailsListMap.putIfAbsent(raSheetDetails.getRaFileDetailsId(), new ArrayList<>());
            rosterSheetDetailsListMap.get(raSheetDetails.getRaFileDetailsId()).add(raSheetDetails);
        }
        List<RAFileAndStats> raFileAndStatsList = new ArrayList<>();
        Map<Long, RAFileDetailsLob> raFileDetailsLobMap = getRAFileDetailsLobMap(raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList()));
        Map<Long, List<RARTFileAltIds>> rartFileAltIdsListMap = getRARTFileAltIdsListMap(raFileDetailsList.stream().map(p -> p.getId()).collect(Collectors.toList()));
        for (RAFileDetails raFileDetails : raFileDetailsList) {
            String lob = raFileDetailsLobMap.containsKey(raFileDetails.getId()) ? raFileDetailsLobMap.get(raFileDetails.getId()).getLob() : "-";
            List<RARTFileAltIds> rartFileAltIdsList = rartFileAltIdsListMap.containsKey(raFileDetails.getId()) ? rartFileAltIdsListMap
                    .get(raFileDetails.getId()).stream().filter(p -> p.getAltIdType().equals(AltIdType.RO_ID.name())).collect(Collectors.toList()) : new ArrayList<>();
            String plmTicketId = rartFileAltIdsList.size() > 0 ? rartFileAltIdsList.get(0).getAltId() : "-";
            raFileAndStatsList.add(getRAFileAndStats(raFileDetails, lob, plmTicketId, rosterSheetDetailsListMap.getOrDefault(raFileDetails.getId(), new ArrayList<>())));
        }
        return raFileAndStatsList;
    }

    public Map<Long, List<RARTFileAltIds>> getRARTFileAltIdsListMap(List<Long> raFileDetailsIdList) {
        List<RARTFileAltIds> raRTFileAltIdsList = rartFileAltIdsRepository.findByRAFileDetailsIdList(raFileDetailsIdList);
        Map<Long, List<RARTFileAltIds>> raRTFileAltIdsListMap = new HashMap<>();
        for (RARTFileAltIds rartFileAltIds : raRTFileAltIdsList) {
            raRTFileAltIdsListMap.putIfAbsent(rartFileAltIds.getRaFileDetailsId(), new ArrayList<>());
            raRTFileAltIdsListMap.get(rartFileAltIds.getRaFileDetailsId()).add(rartFileAltIds);
        }
        return raRTFileAltIdsListMap;
    }

    public Map<Long, RAFileDetailsLob> getRAFileDetailsLobMap(List<Long> raFileDetailsIdList) {
        List<RAFileDetailsLob> raFileDetailsLobList = raFileDetailsLobRepository.findRAFileDetailsLobByFileId(raFileDetailsIdList);
        Map<Long, RAFileDetailsLob> raFileDetailsLobMap = new HashMap<>();
        for (RAFileDetailsLob raFileDetailsLob : raFileDetailsLobList) {
            raFileDetailsLobMap.put(raFileDetailsLob.getRaFileDetailsId(), raFileDetailsLob);
        }
        return raFileDetailsLobMap;
    }

    public RAFileAndErrorStats getRAFileAndErrorStats(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        return getRAFileAndErrorStatsFromSheetDetailsList(raFileDetails, raSheetDetailsList);
    }

    public RAFileAndStats getRAFileAndStats(RAFileDetails raFileDetails, String lob, String plmTicketId,
                                            List<RASheetDetails> raSheetDetailsList) {
        return getRAFileAndStatsFromSheetDetailsList(raFileDetails, lob, plmTicketId, raSheetDetailsList);
    }

    public List<RARTConvProcessingDurationStats> getRosConvProcessingDurationStatsList(long raSheetDetailsId) {
        return raConvProcessingDurationStatsRepository.getRAConvProcessingDurationStatsList(raSheetDetailsId);
    }

    public static long getRosterReceivedTime(RAFileDetails raFileDetails) {
        return raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : 0;
    }
    public static RASheetProgressInfo getBaseRosterSheetProgressInfo(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        //TODO received time when created is null
        long rosterReceivedTime = getRosterReceivedTime(raFileDetails);
        return new RASheetProgressInfo(raSheetDetails.getId(), raSheetDetails.getTabName(), raFileDetails.getStandardizedFileName(),
                rosterReceivedTime);
    }

    public RASheetProgressInfo getRASheetProgressInfo(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        RASheetProgressInfo rosterFileProgressInfo = getBaseRosterSheetProgressInfo(raFileDetails, raSheetDetails);

        long rosterReceivedTime = getRosterReceivedTime(raFileDetails);
        RosterReceivedStageInfo rosterReceivedStageInfo = new RosterReceivedStageInfo(rosterReceivedTime, raSheetDetails.getRosterRecordCount());
        rosterFileProgressInfo.setRosterReceived(rosterReceivedStageInfo);
        List<ErrorSummaryElement> errorSummary = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            errorSummary.add(new ErrorSummaryElement("Category " + i, "Error Type " + i, i));
        }
        rosterFileProgressInfo.setErrorSummary(errorSummary);
        List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList = getRosConvProcessingDurationStatsList(raSheetDetails.getId());
        RosterStageState autoMappedRosterStageState = getRosterStageState(RosterSheetProcessStage.AUTO_MAPPED, raSheetDetails.getStatusCode());
        if (autoMappedRosterStageState != RosterStageState.NOT_STARTED) {
            ProcessDurationInfo processDurationInfo = computeProcessDurationInfo(raConvProcessingDurationStatsList, RosterSheetProcessStage.AUTO_MAPPED);
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.AUTO_MAPPED, -1,
                    processDurationInfo.getStartTime(),
                    autoMappedRosterStageState, processDurationInfo.getTimeTakenInMillis());
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo, Utils.MILLIS_IN_HOUR);
            rosterFileProgressInfo.setAutoMapped(new AutoMappedStageInfo(rosterFileProcessIntermediateStageInfo));
        }
        RosterStageState isfRosterStageState = getRosterStageState(RosterSheetProcessStage.ISF_GENERATED, raSheetDetails.getStatusCode());
        if (isfRosterStageState != RosterStageState.NOT_STARTED) {
            ProcessDurationInfo processDurationInfo = computeProcessDurationInfo(raConvProcessingDurationStatsList, RosterSheetProcessStage.ISF_GENERATED);
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.ISF_GENERATED, raSheetDetails.getIsfRowCount(), processDurationInfo.getStartTime(),
                    isfRosterStageState, processDurationInfo.getTimeTakenInMillis());
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo, Utils.MILLIS_IN_HOUR);
            rosterFileProgressInfo.setIsf(new ISFStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState dartRosterStageState = getRosterStageState(RosterSheetProcessStage.CONVERTED_DART, raSheetDetails.getStatusCode());
        if (dartRosterStageState != RosterStageState.NOT_STARTED) {
            ProcessDurationInfo processDurationInfo = computeProcessDurationInfo(raConvProcessingDurationStatsList, RosterSheetProcessStage.CONVERTED_DART);
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.CONVERTED_DART, raSheetDetails.getOutRowCount(), processDurationInfo.getStartTime(),
                    dartRosterStageState, processDurationInfo.getTimeTakenInMillis());
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo,
                    Utils.MILLIS_IN_HOUR);
            rosterFileProgressInfo.setConvertedDart(new ConvertedDartStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState spsLoadRosterStageState = getRosterStageState(RosterSheetProcessStage.SPS_LOAD, raSheetDetails.getStatusCode());
        if (spsLoadRosterStageState != RosterStageState.NOT_STARTED) {
            ProcessDurationInfo processDurationInfo = computeProcessDurationInfo(raConvProcessingDurationStatsList, RosterSheetProcessStage.SPS_LOAD);
            BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(RosterSheetProcessStage.SPS_LOAD, raSheetDetails.getTargetLoadTransactionCount(),
                    processDurationInfo.getStartTime(),
                    spsLoadRosterStageState, processDurationInfo.getTimeTakenInMillis());
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo,
                    Utils.MILLIS_IN_HOUR);
            rosterFileProgressInfo.setConvertedDart(new ConvertedDartStageInfo(rosterFileProcessIntermediateStageInfo));
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

    public RAFileAndStats getRAFileAndStatsFromSheetDetailsList(RAFileDetails raFileDetails, String lob, String plmTicketId, List<RASheetDetails> raSheetDetailsList) {
        RAFileAndStats raFileAndStats = new RAFileAndStats(raFileDetails.getId(), raFileDetails.getOriginalFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : -1, lob, raFileDetails.getMarket(), plmTicketId,
                raStatusService.getDisplayStatus(raFileDetails.getStatusCode()));
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

    public Map<Long, RAFileDetails> getRAFileDetailsMap(List<RAFileDetails> raFileDetailsList) {
        Map<Long, RAFileDetails> raFileDetailsMap = new HashMap<>();
        for (RAFileDetails raFileDetails : raFileDetailsList) {
            raFileDetailsMap.put(raFileDetails.getId(), raFileDetails);
        }
        return raFileDetailsMap;
    }

    public Map<Long, List<RASheetDetails>> getRASheetDetailsListMap(List<RAFileDetails> raFileDetailsList, List<RASheetDetails> raSheetDetailsList) {
        Map<Long, List<RASheetDetails>> raSheetDetailsListMap = new HashMap<>();
        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
            raSheetDetailsListMap.putIfAbsent(raSheetDetails.getRaFileDetailsId(), new ArrayList<>());
            raSheetDetailsListMap.get(raSheetDetails.getRaFileDetailsId()).add(raSheetDetails);
        }
        return raSheetDetailsListMap;
    }
}

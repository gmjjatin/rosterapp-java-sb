package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RAConvProcessingDurationStats;
import com.hilabs.roster.entity.RAConvStatusStageMappingInfo;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.model.RosterFileProcessStage;
import com.hilabs.roster.model.RosterFileProcessStatus;
import com.hilabs.roster.model.RosterFileProcessStatusPosition;
import com.hilabs.roster.repository.RosterConvStatusStageMappingInfoRepository;
import com.hilabs.rostertracker.dto.RAFileAndErrorStats;
import com.hilabs.rostertracker.dto.RAFileAndStats;
import com.hilabs.rostertracker.model.*;
import com.hilabs.roster.repository.RAConvProcessingDurationStatsRepository;
import com.hilabs.rostertracker.utils.RosterUtils;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.hilabs.roster.model.RosterFileProcessStage.getValidRosterFileProcessStageListInOrder;

@Service
@Log4j2
public class RAFileStatsService {
    @Autowired
    private RosterConvStatusStageMappingInfoRepository rosterConvStatusStageMappingInfoRepository;

    @Autowired
    private RAConvProcessingDurationStatsRepository raConvProcessingDurationStatsRepository;

    @Autowired
    private RosterConvStatusStageMappingInfoService rosterConvStatusStageMappingInfoService;

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
        return RosterUtils.getRAFileAndErrorStatsFromSheetDetailsList(raFileDetails, raSheetDetailsList);
    }

    public RAFileAndStats getRAFileAndStats(RAFileDetails raFileDetails, List<RASheetDetails> raSheetDetailsList) {
        return RosterUtils.getRAFileAndStatsFromSheetDetailsList(raFileDetails, raSheetDetailsList);
    }

//    public RosterSourceAndErrorStats getRosterSourceAndErrorStats(RAProvDetails raProvDetails, List<RAFileDetails> raFileDetailsList) {
//        List<Long> rosterFileIdList = raFileDetailsList.stream().map(RAFileDetails::getId).collect(Collectors.toList());
//        List<RosterConvSummaryStats> rosterConvSummaryStatsList = getRosterConvSummaryStatsList(rosterFileIdList);
//        List<RosConvFalloutDetails> rosConvFalloutDetailsList = getRosConvFalloutDetailsList(rosterFileIdList);
//        RosterErrorStats rosterStats = computeRosterErrorStatsFromConvSummaryAndFalloutStats(raFileDetailsList.size(),
//                rosterConvSummaryStatsList, rosConvFalloutDetailsList);
//        return new RosterSourceAndErrorStats(getBasicRAProvInfo(raProvDetails),
//                rosterStats);
//    }
//
//
    public static class CompletionInfoForFailedOrUnknownProcess {
        public List<RosterFileProcessStage> stagesCompleted;
        public RosterFileProcessStage lastFailedStage;

        public boolean isFailedStatusStarted;

        public CompletionInfoForFailedOrUnknownProcess(List<RosterFileProcessStage> stagesCompleted, RosterFileProcessStage lastFailedStage, boolean isFailedStatusStarted) {
            this.stagesCompleted = stagesCompleted;
            this.lastFailedStage = lastFailedStage;
            this.isFailedStatusStarted = isFailedStatusStarted;
        }
    }
    public CompletionInfoForFailedOrUnknownProcess computeStagesCompletedForFailedOrUnknownStatus(List<RAConvProcessingDurationStats> raConvProcessingDurationStatsList) {
        Map<RosterFileProcessStatus, RosterFileProcessStage> rosterFileStatusStagingMap = rosterConvStatusStageMappingInfoService
                .getRosterFileStatusStagingMap();
        Map<RosterFileProcessStage, List<RAConvProcessingDurationStats>> stageDurationStatsListMap = new HashMap<>();
        for (RAConvProcessingDurationStats stats : raConvProcessingDurationStatsList) {
            if (stats.getStatus() == null) {
                log.error("status is null in RAConvProcessingDurationStats {}", stats);
                continue;
            }
            if (!rosterFileStatusStagingMap.containsKey(stats.getStatus())) {
                log.error("rosterFileStatusStagingMap doesn't contain status {}", stats.getStatus());
                continue;
            }
            stageDurationStatsListMap.put(rosterFileStatusStagingMap.get(stats.getStatus()), new ArrayList<>());
            stageDurationStatsListMap.get(rosterFileStatusStagingMap.get(stats.getStatus())).add(stats);
        }
        List<RosterFileProcessStage> completedRosterFileProcessStageList = new ArrayList<>();
        List<RosterFileProcessStage> validRosterFileProcessStageListInOrder = getValidRosterFileProcessStageListInOrder();
        List<RAConvProcessingDurationStats> prevRAConvProcessingDurationStats = new ArrayList<>();
        RosterFileProcessStage lastFailedStage = null;
        boolean isFailedStatusStarted = false;
        for (int i = validRosterFileProcessStageListInOrder.size() - 1; i >= 0; i--) {
            RosterFileProcessStage rosterFileProcessStage = validRosterFileProcessStageListInOrder.get(i);
            if (prevRAConvProcessingDurationStats.size() != 0) {
                completedRosterFileProcessStageList.add(rosterFileProcessStage);
                continue;
            }
            List<RAConvProcessingDurationStats> durationStatsList = stageDurationStatsListMap.containsKey(rosterFileProcessStage) ?
                    stageDurationStatsListMap.get(rosterFileProcessStage) : new ArrayList<>();
            prevRAConvProcessingDurationStats.addAll(durationStatsList);
            boolean isStatusCompleted = false;
            for (RAConvProcessingDurationStats durationStats : durationStatsList) {
                RosterFileProcessStatus rosterFileProcessStatus = durationStats.getStatus();
                Optional<RAConvStatusStageMappingInfo> optionalRosterConvStatusStageMappingInfo = rosterConvStatusStageMappingInfoService
                        .getRosterConvStatusStageMappingInfoForStatus(rosterFileProcessStatus);
                if (optionalRosterConvStatusStageMappingInfo.isPresent() && optionalRosterConvStatusStageMappingInfo.get().getStatusPosition() == RosterFileProcessStatusPosition.FINAL) {
                    isStatusCompleted = raConvProcessingDurationStatsList.stream().anyMatch(p -> p.getStatus() == rosterFileProcessStatus
                            && p.getCompletionDate() != null);
                }
            }
            if (isStatusCompleted) {
                completedRosterFileProcessStageList.add(rosterFileProcessStage);
            } else {
                lastFailedStage = rosterFileProcessStage;
                isFailedStatusStarted = durationStatsList.size() > 0;
            }
        }
        return new CompletionInfoForFailedOrUnknownProcess(completedRosterFileProcessStageList, lastFailedStage, isFailedStatusStarted);
    }

    //TODO handle failed status. Add test
    public RosterFileProcessIntermediateStageInfo computeRosterFileProcessIntermediateStageInfo(RosterFileProcessStage processStage,
                                                                                                List<RAConvProcessingDurationStats> raConvProcessingDurationStatsList,
                                                                                                RosterFileProcessStatus currRosterFileProcessStatus, int noOfRecords) {
        if (currRosterFileProcessStatus == null || currRosterFileProcessStatus == RosterFileProcessStatus.FAILED) {
            CompletionInfoForFailedOrUnknownProcess completionInfoForFailedOrUnknownProcess = computeStagesCompletedForFailedOrUnknownStatus(raConvProcessingDurationStatsList);
            List<RosterFileProcessStage> stagesCompleted = completionInfoForFailedOrUnknownProcess.stagesCompleted;
            RosterFileProcessStage lastFailedStage = completionInfoForFailedOrUnknownProcess.lastFailedStage;
            boolean isFailedStatusStarted = completionInfoForFailedOrUnknownProcess.isFailedStatusStarted;
            if (stagesCompleted.stream().anyMatch(p -> p == processStage)) {
                BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = getBaseRosterFileProcessStageInfo(processStage,
                        raConvProcessingDurationStatsList, RosterFileStageState.COMPLETED);
                return new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo, noOfRecords, Utils.MILLIS_IN_HOUR);
            } else {
                RosterFileStageState rosterFileStageState;
                if (lastFailedStage != processStage) {
                    rosterFileStageState = RosterFileStageState.NOT_STARTED;
                } else {
                    rosterFileStageState = currRosterFileProcessStatus == RosterFileProcessStatus.FAILED ? RosterFileStageState.FAILED
                            : isFailedStatusStarted ? RosterFileStageState.STARTED : RosterFileStageState.NOT_STARTED;
                }
                BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = getBaseRosterFileProcessStageInfo(processStage,
                        raConvProcessingDurationStatsList, rosterFileStageState);
                return new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo, noOfRecords, Utils.MILLIS_IN_HOUR);
            }
        } else {
            RosterFileProcessStage currRosterFileProcessStage = rosterConvStatusStageMappingInfoService.getRosterFileProcessStage(currRosterFileProcessStatus);
            if (currRosterFileProcessStage == processStage) {
                RosterFileStageState ongoingStageState = getRosterFileStageStateBasedOnCurrentStatus(currRosterFileProcessStatus, raConvProcessingDurationStatsList);
                BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = getBaseRosterFileProcessStageInfo(processStage,
                        raConvProcessingDurationStatsList, ongoingStageState);
                return new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo, noOfRecords, Utils.MILLIS_IN_HOUR);
            } else {
                BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = getBaseRosterFileProcessStageInfo(processStage,
                        raConvProcessingDurationStatsList, RosterFileStageState.COMPLETED);
                return new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo, noOfRecords, Utils.MILLIS_IN_HOUR);
            }
        }
    }

    public List<RAConvProcessingDurationStats> getRosConvProcessingDurationStatsList(long raSheetDetailsId) {
        return raConvProcessingDurationStatsRepository.getRAConvProcessingDurationStatsList(raSheetDetailsId);
    }
    public static RASheetProgressInfo getBaseRosterSheetProgressInfo(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        //TODO received time when created is null
        return new RASheetProgressInfo(raSheetDetails.getId(), raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : 0);
    }
//
    public RASheetProgressInfo getRASheetProgressInfo(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        RASheetProgressInfo rosterFileProgressInfo = getBaseRosterSheetProgressInfo(raFileDetails, raSheetDetails);
        List<RAConvProcessingDurationStats> raConvProcessingDurationStatsList = getRosConvProcessingDurationStatsList(raSheetDetails.getId());
        RosterFileProcessStage currRosterFileProcessStage = rosterConvStatusStageMappingInfoService.getRosterFileProcessStage(raSheetDetails.getStatus());

        List<RosterFileProcessStage> possibleRosterFileProcessStageList = (raSheetDetails.getStatus() == null || raSheetDetails.getStatus() == RosterFileProcessStatus.FAILED) ? getValidRosterFileProcessStageListInOrder()
                : rosterConvStatusStageMappingInfoService.getPrecedingRosterFileProcessStageList(currRosterFileProcessStage);

        if (possibleRosterFileProcessStageList.contains(RosterFileProcessStage.AUTO_MAPPED)) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = computeRosterFileProcessIntermediateStageInfo(RosterFileProcessStage.AUTO_MAPPED,
                    //TODO is getRosterRecordCount() right??
                    raConvProcessingDurationStatsList, raSheetDetails.getStatus(), raSheetDetails.getRosterRecordCount());
            rosterFileProgressInfo.setAutoMapped(new AutoMappedStageInfo(rosterFileProcessIntermediateStageInfo));
        }
        if (possibleRosterFileProcessStageList.contains(RosterFileProcessStage.CONVERTED_DART)) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = computeRosterFileProcessIntermediateStageInfo(RosterFileProcessStage.CONVERTED_DART,
                    raConvProcessingDurationStatsList, raSheetDetails.getStatus(), raSheetDetails.getDartRowCount());
            rosterFileProgressInfo.setConvertedDart(new ConvertedDartStageInfo(rosterFileProcessIntermediateStageInfo));
        }
        if (possibleRosterFileProcessStageList.contains(RosterFileProcessStage.SPS_LOAD)) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = computeRosterFileProcessIntermediateStageInfo(RosterFileProcessStage.SPS_LOAD,
                    raConvProcessingDurationStatsList, raSheetDetails.getStatus(), raSheetDetails.getSpsLoadTransactionCount());
            rosterFileProgressInfo.setSpsLoad(new SpsLoadStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        if (possibleRosterFileProcessStageList.contains(RosterFileProcessStage.REPORT)) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo = computeRosterFileProcessIntermediateStageInfo(RosterFileProcessStage.REPORT,
                    raConvProcessingDurationStatsList, raSheetDetails.getStatus(), -1);
            rosterFileProgressInfo.setReport(new ReportStageInfo(rosterFileProcessIntermediateStageInfo));
        }
        return rosterFileProgressInfo;
    }

    public BaseRosterFileProcessStageInfo getBaseRosterFileProcessStageInfo(RosterFileProcessStage rosterFileProcessStage,
                                                                            List<RAConvProcessingDurationStats> RAConvProcessingDurationStatsList, RosterFileStageState rosterFileStageState) {
        List<RAConvStatusStageMappingInfo> raConvStatusStageMappingInfoList = rosterConvStatusStageMappingInfoService
                .getAllRosterConvMappingInfoList(rosterFileProcessStage);
        //TODO important what if startTime is finally MAX_VALUE??
        long startTime = Long.MAX_VALUE;
        long completedTime = -1;
        for (RAConvStatusStageMappingInfo RAConvStatusStageMappingInfo : raConvStatusStageMappingInfoList) {
            Optional<RAConvProcessingDurationStats> optionalRosConvProcessingDurationStats = RAConvProcessingDurationStatsList.stream()
                    .filter(p -> p.getStatus() == RAConvStatusStageMappingInfo.getProcessingStatus()).findFirst();
            if (!optionalRosConvProcessingDurationStats.isPresent()) {
                continue;
            }
            RAConvProcessingDurationStats raConvProcessingDurationStats = optionalRosConvProcessingDurationStats.get();

            if (raConvProcessingDurationStats.getStartDate() != null) {
                startTime = Math.min(startTime, raConvProcessingDurationStats.getStartDate().getTime());
            }
            if (raConvProcessingDurationStats.getCompletionDate() != null) {
                completedTime = Math.max(completedTime, raConvProcessingDurationStats.getCompletionDate().getTime());
            }
        }
        //TODO check for valid start and endTime.
        // TODO important fix time taken in millis
        return new BaseRosterFileProcessStageInfo(rosterFileProcessStage, rosterFileStageState, Utils.MILLIS_IN_HOUR);
    }

    //TODO write test
    //TODO when is failed status returned
    public RosterFileStageState getRosterFileStageStateBasedOnCurrentStatus(RosterFileProcessStatus currentRosterFileProcessStatus,
                                                                            List<RAConvProcessingDurationStats> raConvProcessingDurationStatsList) {
        Optional<RAConvStatusStageMappingInfo> optionalRosterConvStatusStageMappingInfo = rosterConvStatusStageMappingInfoService
                .getRosterConvStatusStageMappingInfoForStatus(currentRosterFileProcessStatus);
        if (!optionalRosterConvStatusStageMappingInfo.isPresent()) {
            log.error("RAConvStatusStageMappingInfo missing for rosterFileProcessStatus {}", currentRosterFileProcessStatus);
            return RosterFileStageState.NOT_STARTED;
        }
        RAConvStatusStageMappingInfo raConvStatusStageMappingInfo = optionalRosterConvStatusStageMappingInfo.get();
        if (raConvStatusStageMappingInfo.getStatusPosition() != RosterFileProcessStatusPosition.FINAL) {
            return RosterFileStageState.STARTED;
        }
        //If final state and completed return completed.
        boolean isStatusCompleted = raConvProcessingDurationStatsList.stream().anyMatch(p -> p.getStatus() == currentRosterFileProcessStatus
                && p.getCompletionDate() != null);
        return isStatusCompleted ? RosterFileStageState.COMPLETED : RosterFileStageState.STARTED;
    }
}

package com.hilabs.rostertracker.service;

import com.hilabs.roster.dto.AltIdType;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import com.hilabs.roster.repository.*;
import com.hilabs.roster.util.ProcessDurationInfo;
import com.hilabs.rostertracker.dto.*;
import com.hilabs.rostertracker.model.*;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private RARTFalloutReportRepository rartFalloutReportRepository;

    @Autowired
    private RAStatusService raStatusService;

    public List<RAFileAndErrorStats> getRAFileAndErrorStats(List<RAFileDetailsWithSheets> raFileDetailsWithSheetsList) {
        List<RAFileAndErrorStats> raFileAndErrorStatsList = new ArrayList<>();
        for (RAFileDetailsWithSheets raFileDetailsWithSheets : raFileDetailsWithSheetsList) {
            RAFileDetails raFileDetails = raFileDetailsWithSheets.getRaFileDetails();
            raFileAndErrorStatsList.add(getRAFileAndErrorStats(raFileDetails, raFileDetailsWithSheets.getRaSheetDetailsList()));
        }
        return raFileAndErrorStatsList;
    }

    public List<RAFileAndStats> getRAFileAndStats(List<RAFileDetailsWithSheets> raFileDetailsWithSheetsList) {
        List<RAFileAndStats> raFileAndStatsList = new ArrayList<>();
        Map<Long, RAFileDetailsLob> raFileDetailsLobMap = getRAFileDetailsLobMap(raFileDetailsWithSheetsList.stream().map(p -> p.getRaFileDetails()
                .getId()).collect(Collectors.toList()));
        Map<Long, List<RARTFileAltIds>> rartFileAltIdsListMap = getRARTFileAltIdsListMap(raFileDetailsWithSheetsList.stream().map(p -> p.getRaFileDetails().getId())
                .collect(Collectors.toList()));
        for (RAFileDetailsWithSheets raFileDetailsWithSheets : raFileDetailsWithSheetsList) {
            RAFileDetails raFileDetails = raFileDetailsWithSheets.getRaFileDetails();
            List<RASheetDetails> raSheetDetailsList = raFileDetailsWithSheets.getRaSheetDetailsList();
            String lob = raFileDetailsLobMap.containsKey(raFileDetails.getId()) ? raFileDetailsLobMap.get(raFileDetails.getId()).getLob() : "-";
            List<RARTFileAltIds> rartFileAltIdsList = rartFileAltIdsListMap.containsKey(raFileDetails.getId()) ? rartFileAltIdsListMap
                    .get(raFileDetails.getId()).stream().filter(p -> p.getAltIdType().equals(AltIdType.RO_ID.name())).collect(Collectors.toList()) : new ArrayList<>();
            String plmTicketId = rartFileAltIdsList.size() > 0 ? rartFileAltIdsList.get(0).getAltId() : "-";
            raFileAndStatsList.add(getRAFileAndStats(raFileDetails, lob, plmTicketId, raSheetDetailsList));
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

    public List<RARTFileAltIds> getRARTFileAltIdsList(Long raFileDetailsId) {
        return rartFileAltIdsRepository.findByRAFileDetailsIdList(Arrays.asList(raFileDetailsId));
    }

    public Map<Long, RAFileDetailsLob> getRAFileDetailsLobMap(List<Long> raFileDetailsIdList) {
        List<RAFileDetailsLob> raFileDetailsLobList = raFileDetailsLobRepository.findRAFileDetailsLobByFileId(raFileDetailsIdList);
        Map<Long, RAFileDetailsLob> raFileDetailsLobMap = new HashMap<>();
        for (RAFileDetailsLob raFileDetailsLob : raFileDetailsLobList) {
            raFileDetailsLobMap.put(raFileDetailsLob.getRaFileDetailsId(), raFileDetailsLob);
        }
        return raFileDetailsLobMap;
    }

    public Optional<RAFileDetailsLob> getRAFileDetailsLob(Long raFileDetailsId) {
        List<RAFileDetailsLob> raFileDetailsLobList = raFileDetailsLobRepository.findRAFileDetailsLobByFileId(Arrays.asList(raFileDetailsId));
        if (raFileDetailsLobList.size() > 0) {
            return Optional.of(raFileDetailsLobList.get(0));
        } else {
            return Optional.empty();
        }
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
        return new RASheetProgressInfo(raSheetDetails.getId(), raSheetDetails.getTabName(), raFileDetails.getStandardizedFileName(),
                raFileDetails.getCreatedDate() != null ? raFileDetails.getCreatedDate().getTime() : 0);
    }

    //TODO
    public List<FalloutReportElement> getFalloutReport(RASheetDetails raSheetDetails, RosterSheetProcessStage rosterSheetProcessStage) {
        if (rosterSheetProcessStage == RosterSheetProcessStage.ROSTER_RECEIVED) {
            return Arrays.asList(new FalloutReportElement("Records", String.valueOf(raSheetDetails.getRosterRecordCount())));
        } else if (rosterSheetProcessStage == RosterSheetProcessStage.AUTO_MAPPED) {
            return Arrays.asList();
        } else if (rosterSheetProcessStage == RosterSheetProcessStage.ISF_GENERATED) {
            //TODO
            Integer falloutCount = rartFalloutReportRepository.countRAFalloutErrorInfo(raSheetDetails.getId(), "ISF");
            return Arrays.asList(
                    new FalloutReportElement("Records", raSheetDetails.getIsfRowCount() == null ? "-" : String.valueOf(raSheetDetails.getIsfRowCount())),
                    new FalloutReportElement("ISF validation fallouts", falloutCount == null ? "-" : String.valueOf(falloutCount))
            );
        } else if (rosterSheetProcessStage == RosterSheetProcessStage.CONVERTED_DART) {
            Integer falloutCount = rartFalloutReportRepository.countRAFalloutErrorInfo(raSheetDetails.getId(), "DART");
            //TODO
            return Arrays.asList(
                    new FalloutReportElement("Records", raSheetDetails.getOutRowCount() == null ? "-" : String.valueOf(raSheetDetails.getOutRowCount())),
                    new FalloutReportElement("Dart validation fallouts", falloutCount == null ? "-" : String.valueOf(falloutCount))
            );
        } else if (rosterSheetProcessStage == RosterSheetProcessStage.SPS_LOAD) {
            return Arrays.asList(
                    new FalloutReportElement("DART rows submitted by DART UI", "-"),
                    new FalloutReportElement("DART UI fallouts", "-"),
                    new FalloutReportElement("SPS transactions", "-"),
                    new FalloutReportElement("Successful transactions", "-"),
                    new FalloutReportElement("Warning", "-"),
                    new FalloutReportElement("Failure", "-"),
                    new FalloutReportElement("Success %", "-")
            );
        }
        return new ArrayList<>();
    }

    public RosterFileProcessIntermediateStageInfo getRosterFileProcessIntermediateStageInfo(RASheetDetails raSheetDetails, RosterSheetProcessStage rosterSheetProcessStage, RosterStageState rosterStageState,
                                                                                            int recordCount, List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList, long fileReceivedTime) {
        ProcessDurationInfo processDurationInfo = null;
        if (rosterSheetProcessStage == RosterSheetProcessStage.ROSTER_RECEIVED) {
            processDurationInfo = new ProcessDurationInfo(fileReceivedTime, fileReceivedTime, 0);
        } else {
            processDurationInfo = computeProcessDurationInfo(raConvProcessingDurationStatsList, rosterSheetProcessStage);
        }

        long endTime = rosterStageState == RosterStageState.COMPLETED ? processDurationInfo.getEndTime() : -1;
        BaseRosterFileProcessStageInfo baseRosterFileProcessStageInfo = new BaseRosterFileProcessStageInfo(rosterSheetProcessStage,
                rosterStageState, recordCount, processDurationInfo.getTimeTakenInMillis(), endTime,
                getFalloutReport(raSheetDetails, rosterSheetProcessStage));

        return new RosterFileProcessIntermediateStageInfo(baseRosterFileProcessStageInfo, Utils.MILLIS_IN_HOUR, true);
    }

    public RASheetProgressInfo getRASheetProgressInfo(RAFileDetails raFileDetails, RASheetDetails raSheetDetails) {
        RASheetProgressInfo rosterFileProgressInfo = getBaseRosterSheetProgressInfo(raFileDetails, raSheetDetails);
        List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList = getRosConvProcessingDurationStatsList(raSheetDetails.getId());
        long rosterReceivedTime = getRosterReceivedTime(raFileDetails);
        RosterStageState rosterReceivedRosterStageState = getRosterStageState(RosterSheetProcessStage.ROSTER_RECEIVED, raSheetDetails.getStatusCode());
        if (rosterReceivedRosterStageState != RosterStageState.NOT_STARTED) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo  = getRosterFileProcessIntermediateStageInfo(raSheetDetails,
                    RosterSheetProcessStage.ROSTER_RECEIVED, rosterReceivedRosterStageState, raSheetDetails.getRosterRecordCount(), raConvProcessingDurationStatsList, rosterReceivedTime);
            rosterFileProgressInfo.setRosterReceived(new RosterReceivedStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState autoMappedRosterStageState = getRosterStageState(RosterSheetProcessStage.AUTO_MAPPED, raSheetDetails.getStatusCode());
        if (autoMappedRosterStageState != RosterStageState.NOT_STARTED) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo  = getRosterFileProcessIntermediateStageInfo(raSheetDetails, RosterSheetProcessStage.AUTO_MAPPED,
                    autoMappedRosterStageState, raSheetDetails.getRosterRecordCount(), raConvProcessingDurationStatsList, rosterReceivedTime);
            rosterFileProgressInfo.setAutoMapped(new AutoMappedStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState isfRosterStageState = getRosterStageState(RosterSheetProcessStage.ISF_GENERATED, raSheetDetails.getStatusCode());
        if (isfRosterStageState != RosterStageState.NOT_STARTED) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo  = getRosterFileProcessIntermediateStageInfo(raSheetDetails,
                    RosterSheetProcessStage.ISF_GENERATED,
                    isfRosterStageState, raSheetDetails.getIsfRowCount(), raConvProcessingDurationStatsList, rosterReceivedTime);
            rosterFileProgressInfo.setIsf(new ISFStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState dartRosterStageState = getRosterStageState(RosterSheetProcessStage.CONVERTED_DART, raSheetDetails.getStatusCode());
        if (dartRosterStageState != RosterStageState.NOT_STARTED) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo  = getRosterFileProcessIntermediateStageInfo(raSheetDetails,
                    RosterSheetProcessStage.CONVERTED_DART,
                    dartRosterStageState, raSheetDetails.getOutRowCount(), raConvProcessingDurationStatsList, rosterReceivedTime);
            rosterFileProgressInfo.setConvertedDart(new ConvertedDartStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        RosterStageState spsLoadRosterStageState = getRosterStageState(RosterSheetProcessStage.SPS_LOAD, raSheetDetails.getStatusCode());
        if (spsLoadRosterStageState != RosterStageState.NOT_STARTED) {
            RosterFileProcessIntermediateStageInfo rosterFileProcessIntermediateStageInfo  = getRosterFileProcessIntermediateStageInfo(raSheetDetails,
                    RosterSheetProcessStage.SPS_LOAD,
                    spsLoadRosterStageState, raSheetDetails.getTargetLoadTransactionCount(), raConvProcessingDurationStatsList, rosterReceivedTime);
            rosterFileProgressInfo.setSpsLoad(new SpsLoadStageInfo(rosterFileProcessIntermediateStageInfo));
        }

        rosterFileProgressInfo.setErrorSummary(rartFalloutReportRepository.getRAFalloutErrorInfoList(raSheetDetails.getId()));
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
                raSheetDetails.getTabName(), status, raSheetDetails.getType());
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

//    public Map<Long, List<RASheetDetails>> getRASheetDetailsListMap(List<RAFileDetails> raFileDetailsList, List<RASheetDetails> raSheetDetailsList) {
//        Map<Long, List<RASheetDetails>> raSheetDetailsListMap = new HashMap<>();
//        for (RASheetDetails raSheetDetails : raSheetDetailsList) {
//            raSheetDetailsListMap.putIfAbsent(raSheetDetails.getRaFileDetailsId(), new ArrayList<>());
//            raSheetDetailsListMap.get(raSheetDetails.getRaFileDetailsId()).add(raSheetDetails);
//        }
//        return raSheetDetailsListMap;
//    }
}

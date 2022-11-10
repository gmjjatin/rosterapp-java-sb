package com.hilabs.rostertracker.service;

import com.hilabs.roster.entity.RARTConvProcessingDurationStats;
import com.hilabs.roster.entity.RAStatusCDMaster;
import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import com.hilabs.roster.repository.RAStatusCDMasterRepository;
import com.hilabs.roster.util.ProcessDurationInfo;
import com.hilabs.roster.util.RAStatusEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RosterStageService {

    @Autowired
    private RAStatusCDMasterRepository raStatusCDMasterRepository;

    private List<RAStatusEntity> sheetRAStatusEntities = null;
    public List<RAStatusEntity> getSheetRaStatusCDMasterList() {
        if (sheetRAStatusEntities != null) {
            return sheetRAStatusEntities;
        }
        List<RAStatusEntity> localSheetRAStatusEntities = new ArrayList<>();
        List<RAStatusCDMaster> raStatusCDMasters = raStatusCDMasterRepository.getAllSheetRAStatusCDMasterList();
        for (RAStatusCDMaster raStatusCDMaster : raStatusCDMasters) {
            localSheetRAStatusEntities.add(new RAStatusEntity(raStatusCDMaster.getStatusCode(),
            RosterSheetProcessStage.getRosterSheetProcessStageFromStr(raStatusCDMaster.getStageName()),
                    raStatusCDMaster.getStatusDescription(),
                    raStatusCDMaster.getIsCompleteStatus() != null && raStatusCDMaster.getIsCompleteStatus() == 1,
                    raStatusCDMaster.getIsFailStatus() != null && raStatusCDMaster.getIsFailStatus() == 1));
        }
        sheetRAStatusEntities = localSheetRAStatusEntities;
        return sheetRAStatusEntities;
    }

    private List<RAStatusEntity> fileRAStatusEntities = null;

    public List<RAStatusEntity> getFileRaStatusCDMasterList() {
        if (fileRAStatusEntities != null) {
            return fileRAStatusEntities;
        }
        List<RAStatusEntity> localFileRAStatusEntities = new ArrayList<>();
        List<RAStatusCDMaster> raStatusCDMasters = raStatusCDMasterRepository.getAllFileRAStatusCDMasterList();
        for (RAStatusCDMaster raStatusCDMaster : raStatusCDMasters) {
            localFileRAStatusEntities.add(new RAStatusEntity(raStatusCDMaster.getStatusCode(),
                    RosterSheetProcessStage.getRosterSheetProcessStageFromStr(raStatusCDMaster.getStageName()),
                    raStatusCDMaster.getStatusDescription(),
                    raStatusCDMaster.getIsCompleteStatus() != null && raStatusCDMaster.getIsCompleteStatus() == 1,
                    raStatusCDMaster.getIsFailStatus() != null && raStatusCDMaster.getIsFailStatus() == 1));
        }
        fileRAStatusEntities = localFileRAStatusEntities;
        return fileRAStatusEntities;
    }
    public RosterStageState getRosterStageState(RosterSheetProcessStage rosterSheetProcessStage,
                                                       Integer statusCode) {
        if (statusCode == null || rosterSheetProcessStage == null) {
            return RosterStageState.NOT_STARTED;
        }
        if (rosterSheetProcessStage == RosterSheetProcessStage.ROSTER_RECEIVED) {
            return RosterStageState.COMPLETED;
        }
        Optional<RosterSheetProcessStage> optionalRosterSheetProcessStage = getRosterSheetProcessStage(statusCode);
        if (!optionalRosterSheetProcessStage.isPresent()) {
            //TODO fix it
            return RosterStageState.NOT_STARTED;
        }
        RosterSheetProcessStage currRosterSheetProcessStage = optionalRosterSheetProcessStage.get();
        if (currRosterSheetProcessStage.rank > rosterSheetProcessStage.rank) {
            return RosterStageState.COMPLETED;
        } else if (currRosterSheetProcessStage.rank < rosterSheetProcessStage.rank) {
            return RosterStageState.NOT_STARTED;
        }
        Optional<RAStatusEntity> optionalRAStatusEntity = getRASheetStatusEntity(statusCode);
        if (!optionalRAStatusEntity.isPresent()) {
            //TODO fix it
            return RosterStageState.STARTED;
        }
        RAStatusEntity raStatusEntity = optionalRAStatusEntity.get();
        if (raStatusEntity.isFailure()) {
            return RosterStageState.FAILED;
        }
        return raStatusEntity.isCompleted() ? RosterStageState.COMPLETED : RosterStageState.STARTED;
    }

    //TODO recheck logic
    public ProcessDurationInfo computeProcessDurationInfo(List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList, RosterSheetProcessStage rosterSheetProcessStage) {
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        List<RAStatusEntity> raStatusEntities = sheetRAStatusEntityList.stream().filter(p -> p.getStage() == rosterSheetProcessStage).collect(Collectors.toList());
        long timeTakenInMillis = 0;
        long startTime = -1;
        long endTime = -1;
        for (RARTConvProcessingDurationStats rartConvProcessingDurationStats : raConvProcessingDurationStatsList) {
            if (rartConvProcessingDurationStats.getStatusCode() == null) {
                continue;
            }
            Integer statusCode = rartConvProcessingDurationStats.getStatusCode();
            if (raStatusEntities.stream().noneMatch(p -> statusCode.equals(p.getCode()))) {
                continue;
            }
            Date completedDate = rartConvProcessingDurationStats.getCompletionDate();
            Date startDate = rartConvProcessingDurationStats.getStartDate();
            if (startDate != null && (startTime == -1 || startTime > startDate.getTime())) {
                startTime = startDate.getTime();
            }
            if (completedDate != null && (endTime == -1 || endTime < completedDate.getTime())) {
                endTime = completedDate.getTime();
            }
            if (completedDate == null || startDate == null) {
                continue;
            }
            timeTakenInMillis += completedDate.getTime() - startDate.getTime();
        }
        log.info("raConvProcessingDurationStatsList size {} raStatusEntities {} rosterSheetProcessStage {} startTime {} endTime {} timeTakenInMillis {}",
                raConvProcessingDurationStatsList.size(), raStatusEntities.size(), rosterSheetProcessStage, startTime, endTime, timeTakenInMillis);
        return new ProcessDurationInfo(startTime, endTime, timeTakenInMillis);
    }

    public List<Integer> getFailedSheetStatusCodes() {
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        return sheetRAStatusEntityList.stream().filter(RAStatusEntity::isFailure).map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public List<Integer> getCompletedSheetStatusCodes() {
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        return sheetRAStatusEntityList.stream().filter(p -> p.isCompleted() && !p.isFailure())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public List<Integer> getFailedFileStatusCodes() {
        List<RAStatusEntity> fileRAStatusEntityList = getFileRaStatusCDMasterList();
        return fileRAStatusEntityList.stream().filter(RAStatusEntity::isFailure)
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

//    public static List<Integer> getCompletedFileStatusCodes() {
//        return fileStatusEntities.stream().filter(p -> p.isCompleted() && !p.isFailure())
//                .map(RAStatusEntity::getCode).collect(Collectors.toList());
//    }

    public List<Integer> getNonFailedFileStatusCodes() {
        List<RAStatusEntity> fileRAStatusEntityList = getFileRaStatusCDMasterList();
        return fileRAStatusEntityList.stream().filter(p -> !p.isFailure())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public List<RAStatusCDMaster> getSheetRAStatusCDMasterList(List<Integer> statusCodes) {
        return raStatusCDMasterRepository.getSheetRAStatusCDMasterList(statusCodes);
    }

    public long computeTimeTakenInMillis(List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList, RosterSheetProcessStage rosterSheetProcessStage) {
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        List<RAStatusEntity> raStatusEntities = sheetRAStatusEntityList.stream().filter(p -> p.getStage() == rosterSheetProcessStage).collect(Collectors.toList());
        long timeTakenInMillis = 0;
        for (RARTConvProcessingDurationStats rartConvProcessingDurationStats : raConvProcessingDurationStatsList) {
            if (rartConvProcessingDurationStats.getStatusCode() == null) {
                continue;
            }
            Integer statusCode = rartConvProcessingDurationStats.getStatusCode();
            if (!raStatusEntities.stream().anyMatch(p -> p.getCode() == statusCode)) {
                continue;
            }
            Date completedDate = rartConvProcessingDurationStats.getCompletionDate();
            Date startDate = rartConvProcessingDurationStats.getStartDate();
            if (completedDate == null || startDate == null) {
                continue;
            }
            timeTakenInMillis += completedDate.getTime() - startDate.getTime();
        }
        return timeTakenInMillis;
    }

    //TODO check logic
    public Optional<RosterSheetProcessStage> getRosterSheetProcessStage(Integer statusCode) {
        if (statusCode == null) {
            return Optional.empty();
        }
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        Optional<RosterSheetProcessStage> optionalRosterSheetProcessStage = sheetRAStatusEntityList.stream().filter(p -> p.getCode() == statusCode).map(RAStatusEntity::getStage).findFirst();
        if (optionalRosterSheetProcessStage.isPresent()) {
            return optionalRosterSheetProcessStage;
        }
        RosterSheetProcessStage res = null;
        for (RosterSheetProcessStage rosterSheetProcessStage : RosterSheetProcessStage.values()) {
            List<RAStatusEntity> raStatusEntities = getRASheetStatusEntityList(rosterSheetProcessStage);
            if (raStatusEntities.size() > 0 && raStatusEntities.get(0).getCode() <= statusCode) {
                res = rosterSheetProcessStage;
            }
        }
        return res != null ? Optional.of(res) : Optional.empty();
    }


    public List<RAStatusEntity> getRASheetStatusEntityList(RosterSheetProcessStage stage) {
        if (stage == null) {
            return null;
        }
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        return sheetRAStatusEntityList.stream()
                .filter(p -> p.getStage() == stage)
                .sorted((l, r) -> {
                    if (l.getCode() == r.getCode()) {
                        return 0;
                    }
                    return l.getCode() < r.getCode() ? -1 : 1;
                }).collect(Collectors.toList());
    }


    public Optional<RAStatusEntity> getRASheetStatusEntity(Integer statusCode) {
        if (statusCode == null) {
            return Optional.empty();
        }
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        return sheetRAStatusEntityList.stream().filter(p -> p.getCode() == statusCode).findFirst();
    }

    public Optional<RAStatusEntity> getRAFileStatusEntity(Integer statusCode) {
        if (statusCode == null) {
            return Optional.empty();
        }
        List<RAStatusEntity> sheetRAStatusEntityList = getSheetRaStatusCDMasterList();
        return sheetRAStatusEntityList.stream().filter(p -> p.getCode() == statusCode).findFirst();
    }
}

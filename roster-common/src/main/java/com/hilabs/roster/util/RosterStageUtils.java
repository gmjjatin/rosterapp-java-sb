package com.hilabs.roster.util;

import com.hilabs.roster.entity.RARTConvProcessingDurationStats;
import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hilabs.roster.util.RAStatusEntity.fileStatusEntities;
import static com.hilabs.roster.util.RAStatusEntity.sheetStatusEntities;

public class RosterStageUtils {
    //TODO complete
    public static RosterStageState getRosterStageState(RosterSheetProcessStage rosterSheetProcessStage,
                                                       Integer statusCode) {
        if (statusCode == null || rosterSheetProcessStage == null) {
            return RosterStageState.NOT_STARTED;
        }
        if (rosterSheetProcessStage == RosterSheetProcessStage.ROSTER_RECEIVED) {
            return RosterStageState.COMPLETED;
        }
        Optional<RosterSheetProcessStage> optionalRosterSheetProcessStage = RAStatusEntity.getRosterSheetProcessStage(statusCode);
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
        Optional<RAStatusEntity> optionalRAStatusEntity = RAStatusEntity.getRASheetStatusEntity(statusCode);
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
    public static ProcessDurationInfo computeProcessDurationInfo(List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList, RosterSheetProcessStage rosterSheetProcessStage) {
        List<RAStatusEntity> raStatusEntities = sheetStatusEntities.stream().filter(p -> p.getStage() == rosterSheetProcessStage).collect(Collectors.toList());
        long timeTakenInMillis = 0;
        long startTime = -1;
        long endTime = -1;
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
        return new ProcessDurationInfo(startTime, endTime, timeTakenInMillis);
    }

    public static List<Integer> getFailedSheetStatusCodes() {
        return sheetStatusEntities.stream().filter(RAStatusEntity::isFailure).map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public static List<Integer> getCompletedSheetStatusCodes() {
        return sheetStatusEntities.stream().filter(p -> p.isCompleted() && !p.isFailure())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public static List<Integer> getFailedFileStatusCodes() {
        return fileStatusEntities.stream().filter(RAStatusEntity::isFailure).map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public static List<Integer> getFailedAndNonCompatibleStatusCodes() {
        return fileStatusEntities.stream().filter(p -> p.isFailure() || p.isNotCompatible())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

//    public static List<Integer> getCompletedFileStatusCodes() {
//        return fileStatusEntities.stream().filter(p -> p.isCompleted() && !p.isFailure())
//                .map(RAStatusEntity::getCode).collect(Collectors.toList());
//    }

    public static List<Integer> getNonFailedFileStatusCodes() {
        return fileStatusEntities.stream().filter(p -> !p.isFailure())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public static List<Integer> getNonFailedWithoutNonCompatibleFileStatusCodes() {
        return fileStatusEntities.stream().filter(p -> !p.isFailure() && !p.isNotCompatible())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }
}

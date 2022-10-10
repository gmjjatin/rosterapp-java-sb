package com.hilabs.roster.util;

import com.hilabs.roster.entity.RARTConvProcessingDurationStats;
import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;

import java.util.List;
import java.util.stream.Collectors;

import static com.hilabs.roster.util.RAStatusEntity.fileStatusEntities;
import static com.hilabs.roster.util.RAStatusEntity.sheetStatusEntities;

public class RosterStageUtils {
    //TODO complete
    public static RosterStageState getRosterStageState(RosterSheetProcessStage rosterSheetProcessStage,
                                                       Integer statusCode) {
        if (statusCode == null) {
            return RosterStageState.NOT_STARTED;
        }
        //TODO demo
        if (rosterSheetProcessStage == RosterSheetProcessStage.ROSTER_RECEIVED || rosterSheetProcessStage == RosterSheetProcessStage.AUTO_MAPPED) {
            return RosterStageState.COMPLETED;
        }
        return RosterStageState.NOT_STARTED;
//        Optional<RAStatusEntity> optionalRAStatusEntity = sheetStatusEntities.stream().filter(p -> p.getCode() == statusCode).findFirst();
//        if (!optionalRAStatusEntity.isPresent()) {
//            return RosterStageState.NOT_STARTED;
//        }
//        RAStatusEntity raStatusEntity = optionalRAStatusEntity.get();
//        raStatusEntity.isCompleted()
//        //TODO write the logic based statusEntities in RAStatusEntity file
//        throw new RuntimeException("Yet to be implemented");
    }

    public static long computeTimeTakenInMillis(List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList, RosterSheetProcessStage rosterSheetProcessStage) {
        //TODO demo
        return 1000 * 60 * 60;
//        throw new RuntimeException("Yet to be implemented");
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

    public static List<Integer> getCompletedFileStatusCodes() {
        return fileStatusEntities.stream().filter(p -> p.isCompleted() && !p.isFailure())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }

    public static List<Integer> getNonFailedFileStatusCodes() {
        return fileStatusEntities.stream().filter(p -> !p.isFailure())
                .map(RAStatusEntity::getCode).collect(Collectors.toList());
    }
}

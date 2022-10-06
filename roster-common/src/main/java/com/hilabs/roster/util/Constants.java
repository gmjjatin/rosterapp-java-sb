package com.hilabs.roster.util;

import com.hilabs.roster.entity.RARTConvProcessingDurationStats;
import com.hilabs.roster.model.RosterSheetProcessStage;
import com.hilabs.roster.model.RosterStageState;

import java.util.List;

import static com.hilabs.roster.util.RAStatusEntity.statusEntities;

public class Constants {
    public static int ROSTER_INGESTION_IN_PROGRESS = 13;
    public static int ROSTER_INGESTION_VALIDATION_FAILED = 15;
    public static int ROSTER_INGESTION_FAILED = 17;
    public static int ROSTER_INGESTION_COMPLETED = 19;

    public static int AI_Mapping_in_Progres = 121;
    public static int AI_Mapping_Completed = 123;
    public static int AI_Mapping_Failed_System_Error = 125;
    public static int AI_Mapping_Failed_Business_Error = 127;
    public static int AI_Mapping_Manual_review_in_Progress = 129;
    public static int AI_Mapping_Manual_review_Failed = 131;
    public static int AI_Mapping_Manual_review_completed = 133;
    public static int AI_Mapping_Manually_updated = 135;
    public static int AI_Mapping_Post_review_validation_in_Progress	= 137;
    public static int AI_Mapping_Post_review_validation_Completed = 139;
    public static int AI_Mapping_Post_review_validation_Failed = 141;

    //TODO complete
    public static RosterStageState getRosterStageState(RosterSheetProcessStage rosterSheetProcessStage,
                                                       Integer statusCode) {
        if (statusCode == null) {
            return RosterStageState.NOT_STARTED;
        }
        //TODO write the logic based statusEntities in RAStatusEntity file
        throw new RuntimeException("Yet to be implemented");
    }

    public static long computeTimeTakenInMillis(List<RARTConvProcessingDurationStats> raConvProcessingDurationStatsList, RosterSheetProcessStage rosterSheetProcessStage) {
        throw new RuntimeException("Yet to be implemented");
    }
}
package com.hilabs.rapipeline.util;

import java.util.Arrays;
import java.util.List;

import static com.hilabs.roster.util.Constants.ROSTER_INGESTION_COMPLETED;

public class PipelineStatusCodeUtil {
    public static List<Integer> preProcessingStatusCodes = Arrays.asList(ROSTER_INGESTION_COMPLETED);
    public static Integer preProcessingInQueueStatus = 20;
    public static List<Integer> preProcessingJob2StatusCodes = Arrays.asList(21);
    public static List<Integer> isfSheetStatusCodes = Arrays.asList(145);
    public static List<Integer> isfFileStatusCodes = Arrays.asList(27);
    public static List<Integer> dartStatusCodes = Arrays.asList(35);


    public static Integer dartUIValidationInProgressSheetStatusCode = 171;
//    public static Integer dartUIValidationFailedSheetStatusCode = 173;
    public static Integer dartUIFeedbackInQueueSheetStatusCode = 174;
    public static Integer dartUIFeedbackReceived  = 175;

    //TODO demo



    public static Integer dartUIValidationCompleteFileStatusCode = 55;
    //TODO demo
    public static Integer readyForSpsSheetStatusCode = 179;
    public static Integer spsInQueueSheetStatusCode = 180;



}

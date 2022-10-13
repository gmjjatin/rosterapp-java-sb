package com.hilabs.rapipeline.util;

import java.util.Arrays;
import java.util.List;

import static com.hilabs.roster.util.Constants.ROSTER_INGESTION_COMPLETED;

public class PipelineStatusCodeUtil {
    public static List<Integer> preProcessingStatusCodes = Arrays.asList(ROSTER_INGESTION_COMPLETED);
    public static List<Integer> preProcessingJob2StatusCodes = Arrays.asList(21);
    public static List<Integer> isfSheetStatusCodes = Arrays.asList(145);
    public static List<Integer> isfFileStatusCodes = Arrays.asList(27, 31);
    public static List<Integer> dartStatusCodes = Arrays.asList(35, 41);

}

package com.hilabs.rapipeline.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hilabs.roster.util.Constants.ROSTER_INGESTION_COMPLETED;

public class PreprocessingUtils {
    public static List<Integer> preProcessingStatusCodes = Arrays.asList(ROSTER_INGESTION_COMPLETED);
    public static List<Integer> preProcessingJob2StatusCodes = Arrays.asList(21);
}

package com.hilabs.rapipeline.preprocessing;

import java.util.ArrayList;
import java.util.List;

import static com.hilabs.roster.util.Constants.ROSTER_INGESTION_COMPLETED;

public class PreprocessingUtils {
    public static List<Integer> preProcessingStatusCodes = new ArrayList<>(ROSTER_INGESTION_COMPLETED);
    public static List<Integer> pre = new ArrayList<>(ROSTER_INGESTION_COMPLETED);
}

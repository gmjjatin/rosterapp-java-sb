package com.anthem.rostertracker.utils;

import com.anthem.rostertracker.entity.RAProvDetails;

import java.util.*;

public class Utils {
    public static long MILLIS_IN_MINUTE = 1000 * 60;
    public static long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    public static long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

    public static LimitAndOffset getLimitAndOffsetFromPageInfo(Integer pageNo, Integer pageSize) {
        return new LimitAndOffset(pageSize, pageNo <= 0 ? 0 : (pageNo - 1) * pageSize);
    }


    public static class StartAndEndTime {
        public long startTime;
        public long endTime;
        public StartAndEndTime(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
    public static StartAndEndTime getAdjustedStartAndEndTime(long startTime, long endTime) {
        if (endTime == -1) {
            endTime = System.currentTimeMillis();
        }
        if (startTime == -1) {
            startTime = endTime - MILLIS_IN_DAY * 100;
        }
        return new StartAndEndTime(startTime, endTime);
    }

    public static String removeAllNonAlphaNumeric(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }
    public static boolean compareAlphanumeric(String str1, String str2) {
        str1 = removeAllNonAlphaNumeric(str1);
        str2 = removeAllNonAlphaNumeric(str2);
        return str1.equals(str2);
    }

    public static List<String> removeDuplicatesFromList(List<String> list) {
        Set<String> set = new HashSet<>(list);
        return new ArrayList<>(set);
    }
}

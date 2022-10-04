package com.hilabs.rapipeline.util;

public class Utils {
    public static String trimToNChars(String str, int maxLength) {
        return str.length() < maxLength ? str : str.substring(0, maxLength);
    }
}

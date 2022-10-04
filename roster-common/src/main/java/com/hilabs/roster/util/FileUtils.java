package com.hilabs.roster.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private FileUtils() {
    }

    public static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    public static String getAdjustedString(String str, int maxLength) {
        String adjustedKey = str;
        if (str.length() > maxLength) {
            adjustedKey = str.substring(0, maxLength);
        }
        return adjustedKey;
    }
}

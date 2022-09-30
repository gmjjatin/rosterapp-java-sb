package com.hilabs.roster.util;

import java.util.Date;

public class DateUtils {


    private DateUtils() {
    }

    public static String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "sun";
                break;
            case 2:
                day = "mon";
                break;
            case 3:
                day = "tue";
                break;
            case 4:
                day = "wed";
                break;
            case 5:
                day = "thu";
                break;
            case 6:
                day = "fri";
                break;
            case 7:
                day = "sat";
                break;
            default:
                day = "N/A";
                break;
        }
        return day;
    }


    public static boolean checkBetween(Date dateToCheck, Date startDate, Date endDate) {
        return dateToCheck.compareTo(startDate) >= 0 && dateToCheck.compareTo(endDate) <= 0;
    }
}

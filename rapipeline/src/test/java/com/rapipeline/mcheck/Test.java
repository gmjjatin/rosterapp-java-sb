package com.hilabs.rapipeline.mcheck;

import com.hilabs.mcheck.model.ContainerConfig;
import com.hilabs.mcheck.model.Period;
import com.hilabs.mcheck.util.FileUtils;
import com.hilabs.mcheck.util.JsonUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Test {

    static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm [dd]");

    private static String getDayOfWeek(int value) {
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
        }
        return day;
    }

    static boolean isTimeOccurred(List<Period> periods) {
        Calendar c = Calendar.getInstance();

        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dayOfWeek = getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));

        for (Period p : periods) {
            List<String> daysOfWeek = p.getDayOfWeek();
            long dayCount = daysOfWeek.stream().parallel()
                    .filter(day -> day.equalsIgnoreCase(dayOfWeek)).count();

            if (dayCount == 0) {
                break;
            } else {
                // Now, check for time of the day

                Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                Calendar endDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));


                String[] startTime = p.getStartTime().split(":");
                int duration = p.getDuration();
                String unit = p.getUnit();
                int startHour = Integer.parseInt(startTime[0]);
                int startMin = Integer.parseInt(startTime[1]);
                startDate.set(Calendar.HOUR_OF_DAY, startHour);
                startDate.set(Calendar.MINUTE, startMin);

                endDate.set(Calendar.HOUR_OF_DAY, startHour);
                endDate.set(Calendar.MINUTE, startMin);

                if ("h".equalsIgnoreCase(unit)) {
                    endDate.set(Calendar.HOUR_OF_DAY, startHour + duration);
                    endDate.set(Calendar.MINUTE, 0);
                } else {
                    endDate.set(Calendar.MINUTE, startMin + duration);
                }

                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println("Ini: " + sdf.format(startDate.getTime()) + "\nNow: " + sdf.format(now.getTime()) + "\nEnd: " + sdf.format(endDate.getTime()));
                return checkBetween(now.getTime(), startDate.getTime(), endDate.getTime());
            }
        }
        return false;
    }

    public static void main(String[] args) {

        try {
            String rawJsonContent = FileUtils.readFile("src/main/resources/static/config.json");
            ContainerConfig config = JsonUtils.convertToPojo(rawJsonContent, ContainerConfig.class);


            while (true) {
                Boolean isOccurred = isTimeOccurred(config.getJobs().get(0).getPeriod());
                System.out.println("Should Execute: " + isOccurred);
                TimeUnit.MINUTES.sleep(1);
            }


        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
//        System.out.println(getNextCount());
    }

    static int getNextCount() {
        int min = 3;
        int current = 4;
        int worker = 3;
        int max = 6;
        return worker - ((worker + current) - max);
    }

    private static boolean checkBetween(Date dateToCheck, Date startDate, Date endDate) {
        return dateToCheck.compareTo(startDate) >= 0 && dateToCheck.compareTo(endDate) <= 0;
    }

}

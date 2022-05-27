package edu.zjut.androiddeveloper_8.Calendar.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateFormatter {

    public static String getStartTime(Date date) {
        return getDateFormatter(date, "yyyy-MM-dd") + " 00:00";
    }


    public static String getEndTime(Date date) {
        return getDateFormatter(date, "yyyy-MM-dd") + " 23:59";
    }

    public static String getDateFormatter(Date date, String format) {
        SimpleDateFormat ft = new SimpleDateFormat(format);
        return ft.format(date);
    }

}

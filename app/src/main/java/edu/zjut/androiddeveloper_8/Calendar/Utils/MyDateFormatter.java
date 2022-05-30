package edu.zjut.androiddeveloper_8.Calendar.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateFormatter {

    // TODO 时间格式被固定
    public static String getStartTime(Date date) {
        return getDateFormatter(date, "yyyy-MM-dd") + " 00:00:00";
    }


    public static String getEndTime(Date date) {
        return getDateFormatter(date, "yyyy-MM-dd") + " 23:59:59";
    }

    public static String getDateFormatter(Date date, String format) {
        SimpleDateFormat ft = new SimpleDateFormat(format);
        return ft.format(date);
    }

    public static Date parseDateFormatter(String date, String format) {
        //使用SimpleDateFormat对象
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date result = null;
        try {
            result = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}

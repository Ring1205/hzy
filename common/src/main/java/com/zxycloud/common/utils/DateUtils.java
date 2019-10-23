package com.zxycloud.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author leiming
 * @date 2017/10/11
 */
public class DateUtils {

//    public static void main(String[] args){
//        System.out.println("当天的时间戳:" + System.currentTimeMillis());
//        System.out.println("当天的日期:" + getTime());
//        System.out.println("返回当前系统时间:" + Calendar.getInstance().getTime());
//        System.out.println("2018-09-05的星期:" + getDayOfWeekByDate("2018-09-05","yyyy-MM-dd"));
//        System.out.println("2018-09-05的时间戳:" + parse("2018-09-05","yyyy-MM-dd"));
//        System.out.println("时间戳1537078910200的时间:" + format("1537078910200","yyyy-MM-dd HH:mm:ss"));
//    }

    /**
     * 获取时间戳的日期
     *
     * @param time   1536076800000
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String format(long time, String format) {
        if (time == 0) {
            return "-";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            Date date = new Date(time);
            return dateFormat.format(date);
        }
    }
    /**
     * 获取时间戳的日期
     *
     * @param time 1536076800000
     * @return
     */
    public String format(long time) {
        return format(time, "yyyy-MM-dd HH:mm:ss");
    }
    /**
     * 获取时间的时间戳
     *
     * @param time 2018-12-05 12:11:03
     * @param format yyyy-MM-dd HH:mm:ss
     * @return 1536076800000
     */
    public static long parse(String time, String format){
        try {
            return new SimpleDateFormat(format, Locale.getDefault()).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param time   1536076800000
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String format(String time, String format) {
        if (time.isEmpty() || time.trim().length() < 2) {
            return "-";
        } else {
            return format(Long.parseLong(time), format);
        }
    }

    /**
     * 分别获取年、月、日
     *
     * @return
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        return Calendar.getInstance().get(Calendar.DATE);
    }
    /**
     * 获取当前年月日
     * @return 2019-01-05
     */
    public String getTime(){
        return format(System.currentTimeMillis(),"yyyy-MM-dd");
    }

    /**
     * 获取当前周
     *
     * @return 1~7
     */
    public static int getWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取对应日期的星期
     *
     * @param date   2018-09-05
     * @param format yyyy-MM-dd
     * @return 星期三
     */
    public static String getDayOfWeekByDate(String date, String format) {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat(format);
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayOfweek;
    }

}

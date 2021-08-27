package com.toolsbox.contractor.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.toolsbox.contractor.common.Constant.DATE_FORMAT_DEFAULT;

/**
 * Created by Peng X. on 01/27/2019.
 */
public class TimeHelper {

    private static final String TAG = TimeHelper.class.getCanonicalName();

    private static final String FORMAT_DD_MM_YYYY_HH_MM = "dd-MM-yyyy hh:mm";
    private static final String EEE_MMM_D_H_MAAA = "EEE MMM d, h:maaa";
    private static final String YYYY_MM_DD_KK_MM = "yyyy-MM-dd kk:mm";
    private static final String FORMAT_DD_MM_YYYY_HH_MM_2 = "yyyyMMddhhmmss";
    private static final String YYYY_MM_DD_DD_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD_T_DD_MM = "yyyy-MM-dd'T'HH:mm";
    private static final String FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy_MM_dd_HH_mm_ss";
    private static final String MMM_d_at_H_MM_A = "MMM d 'at' h:mm a";

    public static String getISO8601Time(long dateInMill) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_ISO8601, Locale.getDefault());
        Date date = new Date(dateInMill);
        return df.format(date);
    }

    public static String getCurrentStringTime() {
        SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault());
        Calendar now = Calendar.getInstance();
        Date date = new Date(now.getTimeInMillis());
        return df.format(date);
    }

    public synchronized static String getCurrentISO8601Time() {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_ISO8601, Locale.getDefault());
        Calendar now = Calendar.getInstance();
        Date date = new Date(now.getTimeInMillis());
        return df.format(date);
    }

    public synchronized static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_DD_MM_SS, Locale.getDefault());
        Calendar now = Calendar.getInstance();
        Date date = new Date(now.getTimeInMillis());
        return df.format(date);
    }

    public static long getISO8601Time(String inputDate) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_ISO8601, Locale.getDefault());
        Date startDate = new Date();
        try {
            startDate = df.parse(inputDate);
        } catch (Exception e) {
        }
        return startDate.getTime();
    }

    public static long getDateTime(String inputDate) {
        SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_DD_MM_SS, Locale.getDefault());
        Date startDate = new Date();
        try {
            startDate = df.parse(inputDate);
        } catch (Exception e) {
        }
        return startDate.getTime();
    }

    public static String convertToTitleCase(String input) {
        String[] strArray = input.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap;
            switch (s.toLowerCase()) {
                case "a.m.":
                    cap = "AM";
                    break;
                case "p.m.":
                    cap = "PM";
                    break;
                default:
                    cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    break;
            }
            builder.append(cap).append(" ");
        }
        return builder.toString();
    }

    public static String getDateFromMills(long time) {
        try {
            Date date = new Date(time);
            SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
            return convertToTitleCase(df.format(date));
        } catch (Exception e) {
            return "";
        }
    }

    public static String convertToAmPmString(String input) {
        try {
            Locale locale = Locale.getDefault();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", locale);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM dd, hh:mm a", locale);

            Date inputDate = inputFormat.parse(input);
            return convertToTitleCase(outputFormat.format(inputDate));
        } catch (Exception e) {
            return input;
        }
    }

    public static String getFormatTimestamp(String strTime) {
        try {
            long time = getDateTime(strTime);
            Date date = new Date(time);
            Locale locale = Locale.getDefault();
            String language = locale.getLanguage();
            SimpleDateFormat df;
            if (language.equals("nl") || language.equals("NL"))
                df = new SimpleDateFormat("EEE, d MMM, HH:mm", locale);
            else
                df = new SimpleDateFormat("EEEE, MMM. d, hh:mm a", locale);

            return df.format(date);

        } catch (Exception e) {
            return "";
        }
    }

    public static String getTime(String strTime) {
        try {
            long time = getDateTime(strTime);
            Date date = new Date(time);
            Locale locale = Locale.getDefault();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", locale);
            return df.format(date);

        } catch (Exception e) {
            return "";
        }
    }

    public static String convertFrindlyTime(String strTime){
        SimpleDateFormat spf=new SimpleDateFormat(YYYY_MM_DD_T_DD_MM);
        spf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        try {
            date = spf.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat spf1=new SimpleDateFormat(MMM_d_at_H_MM_A);
        String strDate = spf1.format(date);
        return strDate;
    }

    public static Date setTime(Date date, int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        return calendar.getTime();
    }

    public static Date getTomorrow(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    public static boolean checkSameDay(Date date1, Date date2) {
        Date newDate1 = TimeHelper.setTime(date1, 0, 0, 0);
        Date newDate2 = TimeHelper.setTime(date2, 0, 0, 0);
        String strDate1 = GlobalUtils.convertDateToString(newDate1, DATE_FORMAT_DEFAULT);
        String strDate2 = GlobalUtils.convertDateToString(newDate2, DATE_FORMAT_DEFAULT);
        return strDate1.equals(strDate2);
    }

    public static boolean checkSameDate(Date date1, Date date2) {
        String strDate1 = GlobalUtils.convertDateToString(date1, DATE_FORMAT_DEFAULT);
        String strDate2 = GlobalUtils.convertDateToString(date2, DATE_FORMAT_DEFAULT);
        return strDate1.equals(strDate2);
    }


}
package com.toolsbox.contractor.common.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.toolsbox.contractor.R;
import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.common.model.AvailabilityDateInfo;
import com.toolsbox.contractor.common.model.FromToDateInfo;
import com.toolsbox.contractor.common.model.ServiceItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import static com.toolsbox.contractor.common.Constant.DATE_FORMAT_AVAILABILITY;
import static com.toolsbox.contractor.common.Constant.DATE_FORMAT_TIME_STAMP;

public class GlobalUtils {
    private static String TAG = "GlobalUtils";

    // for industry
    public static String getFormatedStringFromIndustry(ArrayList<Integer> arrIndustry){
        String strIndustries = "";
        for (int i = 0; i < arrIndustry.size(); i++){
            if (i == 0){
                strIndustries = String.valueOf(arrIndustry.get(i));
            } else {
                strIndustries += "," + String.valueOf(arrIndustry.get(i));
            }
        }
        return strIndustries;
    }

    public static List<Integer> getIntArrayIndustryFromString(String strIndustry){
        List<String> arrTemp = new LinkedList<>(Arrays.asList(strIndustry.split(",")));
        List<Integer> arrIndustry = new ArrayList<>();
        for (int i = 0; i < arrTemp.size(); i++){
            int value = Integer.valueOf(arrTemp.get(i));
            arrIndustry.add(value);
        }

        return arrIndustry;
    }

    // for availability
    public static String getFormatedStringFromDates(ArrayList<Date> arrDates, String format){
        String strDates = "";
        for (int i = 0; i < arrDates.size(); i++){
            if (i == 0){
                strDates = covertDateToString(arrDates.get(i), format);
            } else {
                strDates += "," + covertDateToString(arrDates.get(i), format);
            }
        }
        return strDates;
    }

    public static List<CalendarDay> getArrayCalendarDateFromString(String strCommentDate, String format){
        List<String> arrStrDates = new LinkedList<>(Arrays.asList(strCommentDate.split(",")));
        List<CalendarDay> arrDate = new ArrayList<>();
        for (String strDate : arrStrDates){
            arrDate.add(CalendarDay.from(covnertStringToDate(strDate, format)));
        }
        return arrDate;
    }

    public static String covertDateToString(Date date, String format){
        SimpleDateFormat spf=new SimpleDateFormat(format);
        String strDate = spf.format(date);
        return strDate;
    }

    public static Date covnertStringToDate(String strDate, String format){
        SimpleDateFormat spf=new SimpleDateFormat(format);
        try {
            Date date = spf.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String lastChatDate(Date date){
        String value = "";
        int distanceDay = daysBetweenDates(new Date(), date);
        if (distanceDay == 0){
            value = covertDateToString(date, DATE_FORMAT_TIME_STAMP);
        } else if (distanceDay == 1){
            value = "Yesterday";
        } else if (distanceDay < 7) {
            value = covertDateToString(date, Constant.APP_WEEKEND_FORMAT);
        } else {
            value = covertDateToString(date, Constant.APP_DATE_FORMAT_MIDDLE);
        }
        return value;
    }

    public static String lastChatDate2(Date date){
        String value = "";
        int distanceDay = daysBetweenDates(new Date(), date);
        if (distanceDay == 0){
            value = "Today";
        } else {
            value = covertDateToString(date, Constant.DATE_FORMAT_MMM_D_YYYY);
        }
        return value;
    }


    public static int daysBetweenDates(Date fromDate, Date toDate){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(fromDate);
        Calendar endCalendar  = new GregorianCalendar();
        endCalendar .setTime(toDate);
        long diff = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        return (int)dayCount;
    }

    // Chat
    public static String getChatIdentity(){
        int id = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        return "S" + id;
    }

    public static String getChatIdentityById(int id, int type){
        if (type == 0)
            return "C" + id;
        else
            return "S" + id;
    }

    public static String getEndpointIdentity(String chatUniqueName){
        String removeMyIdentity = chatUniqueName.replace(getChatIdentity(), "");
        String result = removeMyIdentity.replace("-", "");
        return result;
    }


    public static String makeChatUniqueName(int endpointId, int endpointType){
        int myId = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        if (endpointType == 0) { // Customer
            return  "C" + endpointId+ "-" + getChatIdentity();
        } else {  // Specialist
            if (myId > endpointId){
                return  "S" + endpointId+ "-" + getChatIdentity();
            } else {
                return  getChatIdentity() + "-" + "S" + endpointId;
            }
        }
    }

    public static boolean isMe(String identity){
        return identity.equals(getChatIdentity());
    }

    public static void printObject(String tag, Object object){
        Gson gson = new Gson();
        String temp = gson.toJson(object);
        Log.e("===>", tag + temp);
    }

    public static String convertScheduleRange(String strScheduledDate) {
        List<String> arrData = new ArrayList<>();
        if (!StringHelper.isEmpty(strScheduledDate)) {
            arrData = Arrays.asList(strScheduledDate.split("-"));
        }
        if (arrData.size() != 2) {
            return "Undefine Value";
        }
        Date fromDate = convertStringToDate(arrData.get(0), DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
        Date toDate = convertStringToDate(arrData.get(1), DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
        String strFrom = convertDateToString(fromDate, "MMM d 'at' h a");
        String strTo = convertDateToString(toDate, "h a");
        return strFrom + " - " + strTo;

    }

    public static Date convertStringToDate(String strDate, String strFormat, TimeZone tz){
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        format.setTimeZone(tz);
        try {
            Date date = format.parse(strDate);
            System.out.println(date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String convertDateToString(Date date, String format){
        SimpleDateFormat spf=new SimpleDateFormat(format);
        String strDate = spf.format(date);
        return strDate;
    }

    public static String convertDateToString(Date newDate, String strFormat, TimeZone tz){
        SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
        dateFormat.setTimeZone(tz);
        String dateTime = dateFormat.format(newDate);
        System.out.println("Date Time : " + dateTime);
        return dateTime;
    }

    public static String getStringStamp(FromToDateInfo item) {
        return String.format("%s%s%s", GlobalUtils.convertDateToString(item.fromDate, DATE_FORMAT_TIME_STAMP), " - ", GlobalUtils.convertDateToString(item.toDate, DATE_FORMAT_TIME_STAMP));
    }

    public static FromToDateInfo convertStringToAvailabilityDateInfo(String str){
        if (StringHelper.isEmpty(str))
            return null;
        String[] data = str.split("-");
        if (data.length != 2)
            return null;
        Date fromDate = convertStringToDate(data[0], DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
        Date toDate = convertStringToDate(data[1], DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
        FromToDateInfo stamp = new FromToDateInfo(fromDate, toDate);
        return stamp;
    }

    public static ArrayList<AvailabilityDateInfo> convertAvailabilityDateModel(String str) {
        ArrayList<AvailabilityDateInfo> result = new ArrayList<>();
        List<String> arrUTCStamps = Arrays.asList(str.split(","));
        ArrayList<FromToDateInfo> arrFromToDate = new ArrayList<>();
        for (String item : arrUTCStamps) {
            List<String> arrDate = Arrays.asList(item.split("-"));
            Date fromDate = convertStringToDate(arrDate.get(0), Constant.DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
            Date toDate = convertStringToDate(arrDate.get(1), Constant.DATE_FORMAT_AVAILABILITY, TimeZone.getTimeZone("UTC"));
            FromToDateInfo stamp = new FromToDateInfo(fromDate, toDate);
            arrFromToDate.add(stamp);
        }

        for (int i = 0; i < arrFromToDate.size(); i++) {
            // Skip same value
            boolean isExist = false;
            if (i > 0) {
                for (int k = 0; k < i; k++) {
                    if (TimeHelper.checkSameDay(arrFromToDate.get(i).fromDate, arrFromToDate.get(k).fromDate)) {
                        isExist = true;
                    }
                }
            }
            if (isExist) {
                continue;
            }
            ArrayList<FromToDateInfo> temp = new ArrayList<>();
            temp.add(arrFromToDate.get(i));
            for (int j = i + 1; j < arrFromToDate.size(); j++) {
                if (TimeHelper.checkSameDay(arrFromToDate.get(i).fromDate, arrFromToDate.get(j).fromDate)) {
                    temp.add(arrFromToDate.get(j));
                }
            }
            result.add(new AvailabilityDateInfo(temp));
        }
        return result;
    }

    public static String fetchDownloadFileName(String url) {
        if (StringHelper.isEmpty(url)) {
            return "...";
        } else {
            List<String> arr = Arrays.asList(url.split("/"));
            if (arr.size() > 2) {
                return arr.get(arr.size() - 1);
            } else {
                return "...";
            }
        }
    }

    public static ArrayList<ServiceItem> getAllService(){
        ArrayList<ServiceItem> arrService = new ArrayList<>();
        arrService.add(new ServiceItem(1, "Appliance Installation & Repair Service", R.drawable.sev_appliance_repair));
        arrService.add(new ServiceItem(2, "Carpentry", R.drawable.sev_carpentry));
        arrService.add(new ServiceItem(3, "Cleaning Service", R.drawable.sev_cleaning_services));
        arrService.add(new ServiceItem(4, "Concrete", R.drawable.sev_concrete));
        arrService.add(new ServiceItem(5, "Deck Building", R.drawable.sev_deck_building));
        arrService.add(new ServiceItem(6, "Demolition & Hauling", R.drawable.sev_demolition_hauling));
        arrService.add(new ServiceItem(7, "Drywall & Plastering", R.drawable.sev_dry_wall));
        arrService.add(new ServiceItem(8, "Electrical", R.drawable.sev_electrical_services));
        arrService.add(new ServiceItem(9, "Fencing", R.drawable.sev_fencing));
        arrService.add(new ServiceItem(10, "Fire & Water Damage Restoration Services", R.drawable.sev_fire_damage_restoration_services));
        arrService.add(new ServiceItem(11, "Fire Alarm Systems", R.drawable.sev_fire_alarm_systems));
        arrService.add(new ServiceItem(12, "Flooring & Tiling", R.drawable.sev_flooring_tiling));
        arrService.add(new ServiceItem(13, "Handyman", R.drawable.sev_handyman));
        arrService.add(new ServiceItem(14, "Home Automation", R.drawable.sev_home_automation));
        arrService.add(new ServiceItem(15, "Home Renovation", R.drawable.sev_home_insulation));
        arrService.add(new ServiceItem(16, "Home Security & Camera Installation", R.drawable.sev_home_security));
        arrService.add(new ServiceItem(17, "Interior Design & Decoration", R.drawable.sev_interior_decoration));
        arrService.add(new ServiceItem(18, "HVAC - Heating Ventilation Air Conditioning", R.drawable.sev_heating_ventilation_air_conditioning));
        arrService.add(new ServiceItem(19, "Lawn Maintenance & Landscaping", R.drawable.sev_lawn_maintenance));
        arrService.add(new ServiceItem(20, "Masonry & Bricklaying", R.drawable.sev_masonry_bricklaying));
        arrService.add(new ServiceItem(21, "Mould Control", R.drawable.sev_wedding_planner));
        arrService.add(new ServiceItem(22, "Waste Removal & Recycling", R.drawable.sev_mold_removal_services));
        arrService.add(new ServiceItem(23, "Mobile Car Wash", R.drawable.sev_mobile_car_wash));
        arrService.add(new ServiceItem(24, "Moving & Storage Services", R.drawable.sev_moving_services));
        arrService.add(new ServiceItem(25, "Locksmith", R.drawable.sev_musician));
        arrService.add(new ServiceItem(26, "Painting", R.drawable.sev_painting));
        arrService.add(new ServiceItem(27, "Paving", R.drawable.sev_paving));
        arrService.add(new ServiceItem(28, "Pest Control", R.drawable.sev_pest_control));
        arrService.add(new ServiceItem(29, "Plumbing", R.drawable.sev_plumbing));
        arrService.add(new ServiceItem(30, "Roofing", R.drawable.sev_roofing));
        arrService.add(new ServiceItem(31, "Snow Removal", R.drawable.sev_snow_clearing));
        arrService.add(new ServiceItem(32, "Windows & Doors Installation & Repairs", R.drawable.sev_windows_doors_installation));
        return  arrService;
    }

}

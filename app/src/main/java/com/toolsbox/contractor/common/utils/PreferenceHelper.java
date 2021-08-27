package com.toolsbox.contractor.common.utils;

import com.toolsbox.contractor.common.Constant;

import java.util.Arrays;
import java.util.List;

public class PreferenceHelper {
    //Preference
    public static void removePreference(){
        AppPreferenceManager.setInt(Constant.PRE_ID, 0);
        AppPreferenceManager.setString(Constant.PRE_IMAGE_URL, "");
        AppPreferenceManager.setString(Constant.PRE_EMAIL, "");
        AppPreferenceManager.setString(Constant.PRE_PASSWORD, "");
        AppPreferenceManager.setString(Constant.PRE_BUSINESS_NAME, "");
        AppPreferenceManager.setInt(Constant.PRE_BUSINESS_STRUCTURE, 0);
        AppPreferenceManager.setString(Constant.PRE_INDUSTRY, "");
        AppPreferenceManager.setString(Constant.PRE_SPECIALITY_TITLE, "");
        AppPreferenceManager.setString(Constant.PRE_PHONE, "");
        AppPreferenceManager.setString(Constant.PRE_ADDRESS, "");
        AppPreferenceManager.setString(Constant.PRE_ADDRESS_LATI, "");
        AppPreferenceManager.setString(Constant.PRE_ADDRESS_LONGI, "");
        //    AppPreferenceManager.setString(Constant.PRE_FCM_TOKEN, Constant.DEFAULT_FCM_TOKEN);
        AppPreferenceManager.setString(Constant.PRE_TOKEN, "");
    }

    public static void savePreference(int id, String imageURL, String email, String password, String businessName,
                                      String businessNumber, int businessStructure, String industry, String specialityTitle,
                                      String phone, String address, String addressLati, String addressLongi,
                                      String fcmToken, String token){
        AppPreferenceManager.setInt(Constant.PRE_ID, id);
        AppPreferenceManager.setString(Constant.PRE_IMAGE_URL, imageURL);
        AppPreferenceManager.setString(Constant.PRE_EMAIL, email);
        AppPreferenceManager.setString(Constant.PRE_PASSWORD, password);
        AppPreferenceManager.setString(Constant.PRE_BUSINESS_NAME, businessName);
        AppPreferenceManager.setString(Constant.PRE_BUSINESS_NUMBER, businessNumber);
        AppPreferenceManager.setInt(Constant.PRE_BUSINESS_STRUCTURE, businessStructure);
        AppPreferenceManager.setString(Constant.PRE_INDUSTRY, industry);
        AppPreferenceManager.setString(Constant.PRE_SPECIALITY_TITLE, specialityTitle);
        AppPreferenceManager.setString(Constant.PRE_PHONE, phone);
        AppPreferenceManager.setString(Constant.PRE_ADDRESS, address);
        AppPreferenceManager.setString(Constant.PRE_ADDRESS_LATI, addressLati);
        AppPreferenceManager.setString(Constant.PRE_ADDRESS_LONGI, addressLongi);
        AppPreferenceManager.setString(Constant.PRE_TOKEN, token);
    }

    public static boolean isLoginIn(){
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        return !StringHelper.isEmpty(token);
    }

    public static int getId(){
        int id = AppPreferenceManager.getInt(Constant.PRE_ID, 0);
        return id;
    }

    public static String getName(){
        String name = AppPreferenceManager.getString(Constant.PRE_BUSINESS_NAME, "");
        return name;
    }

    public static String getBusinessNumber(){
        return AppPreferenceManager.getString(Constant.PRE_BUSINESS_NUMBER, "");
    }

    public static int getBusinessStructure(){
        return AppPreferenceManager.getInt(Constant.PRE_BUSINESS_STRUCTURE, 0);
    }

    public static String getAddress(){
        return AppPreferenceManager.getString(Constant.PRE_ADDRESS, "");
    }

    public static String getEmail(){
        String email = AppPreferenceManager.getString(Constant.PRE_EMAIL, "");
        return email;
    }

    public static String getPhone(){
        String phone = AppPreferenceManager.getString(Constant.PRE_PHONE, "");
        return phone;
    }

    public static String getImageURL(){
        String imageURL = AppPreferenceManager.getString(Constant.PRE_IMAGE_URL, "");
        return imageURL;
    }

    public static String getToken(){
        String token = AppPreferenceManager.getString(Constant.PRE_TOKEN, "");
        return token;
    }


    public static int getIndustry(){
        String strIndustry = AppPreferenceManager.getString(Constant.PRE_INDUSTRY, "");
        if (StringHelper.isEmpty(strIndustry)) {
            return 0;
        }
        List<String> arrIndustry = Arrays.asList(strIndustry.split(","));
        return Integer.parseInt(arrIndustry.get(0));

    }

}

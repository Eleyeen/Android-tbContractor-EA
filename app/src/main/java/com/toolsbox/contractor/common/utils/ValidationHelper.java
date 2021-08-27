package com.toolsbox.contractor.common.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by LS on 09/15/2017.
 */
public class ValidationHelper {

    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
        return  (!TextUtils.isEmpty(phoneNumber)) && String.valueOf(phoneNumber).length() == 10 ?
                Patterns.PHONE.matcher(phoneNumber).matches() : false;
    }

    public static boolean isValidEmail(String email) {
        return email.contains("@");
    }

    public static boolean isValidPassword(String password) {
        return password.length() > 4;
    }

    public static boolean limitedSize(String str){
        return str.length() > 50;
    }
}

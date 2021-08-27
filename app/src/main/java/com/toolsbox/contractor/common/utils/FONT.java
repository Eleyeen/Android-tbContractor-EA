package com.toolsbox.contractor.common.utils;

import android.graphics.Typeface;

import com.toolsbox.contractor.TBApplication;


/**
 * Created by LS on 10/29/2017.
 */

public class FONT {
    public static Typeface Montserrat_light = Typeface.createFromAsset(TBApplication.gContext.getAssets(),  "fonts/Montserrat-Light.ttf");
    public static Typeface Montserrat_Bold = Typeface.createFromAsset(TBApplication.gContext.getAssets(), "fonts/Montserrat-Bold.ttf");
    public static Typeface Roboto_Bold = Typeface.createFromAsset(TBApplication.gContext.getAssets(), "fonts/Roboto-Bold.ttf");
    public static Typeface Roboto_light = Typeface.createFromAsset(TBApplication.gContext.getAssets(), "fonts/Roboto-Light.ttf");
}

package com.toolsbox.contractor;

import android.content.Context;
import androidx.multidex.MultiDexApplication;

import com.google.android.libraries.places.api.Places;
import com.toolsbox.contractor.common.utils.AppPreferenceManager;
import com.toolsbox.contractor.controller.chat.ChatClientManager;

import static com.toolsbox.contractor.common.Constant.GOOGLE_API_KEY;
import static com.toolsbox.contractor.common.Constant.POSTER_TYPE_ME;
import static com.toolsbox.contractor.common.Constant.POSTER_TYPE_OTHERS;


/**
 * Created by LS on 9/13/2017.
 */

public class TBApplication extends MultiDexApplication {
    private static final String TAG = "TBApplication";

    private ChatClientManager basicClient;
    private static TBApplication instance;
    public static Context gContext;
    public static int selectedPostedby = POSTER_TYPE_OTHERS;
    private static String gActiveChannelSid = "";
    private static boolean gLoggedIn = false;

    /**
     * Multi Dex supporting for some version below KitKat
     * @param base
     */

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (gContext == null)
            gContext = getApplicationContext();
        AppPreferenceManager.initializePreferenceManager(gContext);
        Places.initialize(gContext, GOOGLE_API_KEY);
        TBApplication.instance = this;
        basicClient = new ChatClientManager(getApplicationContext());
    }

    public static TBApplication get() {
        return instance;
    }

    public ChatClientManager getChatClientManager() {
        return this.basicClient;
    }

    public static void setActiveChannelSid(String sid){
        gActiveChannelSid = sid;
    }

    public static String getActiveChannelSid() {
        return gActiveChannelSid;
    }


    public static boolean isgLoggedIn() {
        return gLoggedIn;
    }

    public static void setgLoggedIn(boolean gLoggedIn) {
        TBApplication.gLoggedIn = gLoggedIn;
    }




}

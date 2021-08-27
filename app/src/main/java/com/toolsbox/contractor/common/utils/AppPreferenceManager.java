package com.toolsbox.contractor.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/13/2017.
 */

public class AppPreferenceManager {
    private static SharedPreferences preferences;

    static Context mContext;

    public static void initializePreferenceManager(Context context) {

        mContext = context;
        preferences = context.getSharedPreferences("TelePatient", 0);
    }

    public static void clearAll()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
    public static void setBoolean(String key, boolean value) {


        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }
    public static void setString(String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void setArray(String key, List<String> mArray)
    {
        JSONArray array = new JSONArray(mArray);
        String json = array.toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public static List<String> getArray(String key)
    {
        List<String> array = new ArrayList<>();

        SharedPreferences.Editor editor = preferences.edit();
        String json=preferences.getString(key, "");

        try {

            JSONArray jsonArray=new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                array.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return array;
    }

    public static void setFloatArray(String key, List<Float> mArray)
    {
        JSONArray array = new JSONArray(mArray);
        String json = array.toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public static List<Float> getFloatArray(String key)
    {
        List<Float> array = new ArrayList<>();

        SharedPreferences.Editor editor = preferences.edit();
        String json = preferences.getString(key, "");

        try {
            JSONArray jsonArray=new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++)
                array.add((float)jsonArray.getDouble(i));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return array;
    }

    public static void setBooleanArray(String key, List<Boolean> mArray)
    {
        JSONArray array=new JSONArray(mArray);
        String json=array.toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public static List<Boolean> getBooleanArray(String key)
    {
        List<Boolean> array = new ArrayList<>();

        SharedPreferences.Editor editor = preferences.edit();
        String json=preferences.getString(key, "");

        try {
            JSONArray jsonArray=new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++)
                array.add(jsonArray.getBoolean(i));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return array;
    }
}

package com.toolsbox.contractor.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.toolsbox.contractor.common.Constant;
import com.toolsbox.contractor.view.activity.basic.BaseActivity;

import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by LS on 01/27/2019.
 */
public class DeviceUtil {

    public final static String TAG = "DeviceUtil";

    private static View disableView = null;

    /**
     * description: Show or Hide the soft keyborad as focused onto a EditText parameter
     *
     * @param context
     * @param edt
     * @param visible
     */
    public static void setKeyboard(final Context context, final EditText edt, final boolean visible) {
        final Runnable showIMERunnable = new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(edt, 0);
                }
            }
        };

        final Handler handler = new Handler();

        if (visible) {
            handler.post(showIMERunnable);
        } else {
            handler.removeCallbacks(showIMERunnable);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
            }
        }
    }

    /**
     * description:  Close the soft keyboard in a activity
     *
     * @param context
     */
    public static void closeKeyboard(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = ((BaseActivity) context).getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 300);
    }

    /**
     * description:  Close the soft keyboard on EditText
     *
     * @param context
     * @param editText
     */
    public static void closeKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * description:  Show the soft keyboard on EditText
     *
     * @param context
     * @param editText
     */
    public static void showKeyboard(final Context context, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        }, 300);
    }

    public static void showKeyboard(final Context context, final EditText editText, int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        }, delay);
    }

    /**
     * Open Wifi setting
     *
     * @param context
     */
    public static void startSettingWifi(Context context) {
        try {
            Intent intentWifi = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            context.startActivity(intentWifi);
        } catch (Exception e) {
        }
    }

    /**
     * Open Device setting
     *
     * @param context
     */
    public static void startSettingDevice(Context context) {
        try {
            ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
        } catch (Exception e) {
        }
    }

    /**
     * @param context
     */
    public static void startSettingWireless(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return ((netInfo != null) && netInfo.isConnected());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return ((netInfo != null) && netInfo.isConnected());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isOnline(Context context) {
        return isMobileConnected(context) || isWifiConnected(context);
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDevModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String val = String.format("%02X", (b & 0xFF));
                    res1.append(val).append(":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().toUpperCase();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    public static boolean is7InchLess(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches <= 7;
    }

    public static void KeepScreen(Activity activity, boolean on) {
        if (on)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static String ByteArrayToHexString(byte[] array) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

        StringBuilder out = new StringBuilder();

        for (j = 0; j < array.length; ++j) {
            in = array[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out.append(hex[i]);
            i = in & 0x0f;
            out.append(hex[i]);
            out.append(" ");
        }

        return out.toString().replace(" ", "");
    }

    /**
     * Compares two version strings.
     * <p>
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     * The result is a positive integer if str1 is _numerically_ greater than str2.
     * The result is zero if the strings are _numerically_ equal.
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     */
    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    // Method for opening a pdf file
    public static void ViewPdf(Context context, String fileName) {

        if (StringHelper.isEmpty(fileName))
            return;

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(pdfIntent);
        } catch (Exception e) {
        }
    }

    public static String getExternalStorage(Context context, int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite())
                return sd.getAbsolutePath();
        }

        return "";
    }

    public static String getID() {
        String uid = UUID.randomUUID().toString().replace("-", "");
        String mac_addr = DeviceUtil.getMacAddr().replaceAll(":", "");
        if (StringHelper.isEmpty(mac_addr) || mac_addr.length() != 12) return uid;
        String code = mac_addr.toLowerCase();
        String now = TimeHelper.getCurrentISO8601Time().replaceAll("[^\\d]", "");
        return code + now;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getHexStr(long dat) {
        return String.format("0x%X", dat);
    }

    public static String getIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                return String.format(Locale.ENGLISH, "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            }
        } catch (Exception e) {
        }
        return "0.0.0.0";
    }

    /**
     * Open Date and Time setting
     *
     * @param context
     */
    public static void startDateTimeSetting(Context context) {
        try {
            ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), 0);
        } catch (Exception e) {
        }
    }

    public static void LockOrientation(Activity context) {
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void keepScreen(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static String getAndroidUniqueID(Context context) {
        // UUID.randomUUID().toString().replace("-", "")
        String id = AppPreferenceManager.getString(Constant.kAndroidID, "");
        if (!id.equals(""))
            return id;
        else if (context != null) {
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            AppPreferenceManager.setString(Constant.kAndroidID, id);
        }

        return id;
    }

}

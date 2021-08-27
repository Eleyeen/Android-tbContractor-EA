package com.toolsbox.contractor.common.utils;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Administrator on 9/14/2017.
 */

public class StringHelper {
    public static final String BLANK = "";
    public static final String NULL  = "null";
    public static final String SPAC  = "[|]";
    public static final String SPAC2 = "[/]";


    private static final char[] symbols;
    private static final char[] symbolsNumber;
    private static final Random mRandom = new Random();
    private static char[] mChBuffer     = null;

    static {
        char ch;
        StringBuilder tmp = new StringBuilder();
        for (ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (ch = 'A'; ch <= 'Z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();

        StringBuilder tmpNum = new StringBuilder();
        for (ch = '0'; ch <= '9'; ++ch)
            tmpNum.append(ch);
        symbolsNumber = tmpNum.toString().toCharArray();
    }

    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
                .append(Character.toTitleCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

    public static String md5(String s) {
        try { // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            return BLANK;
        }
    }

    public static String decodeString(String str) {
        String decodedString = BLANK;
        try {
            decodedString = URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
        }
        return decodedString;
    }

    public static String encodeString(String str) {
        String encodedString = BLANK;
        try {
            encodedString = URLEncoder.encode(str, "UTF-8");
            encodedString = encodedString.replace("+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            encodedString = str;
        }
        return encodedString;
    }

    public static boolean isEmpty(String str) {
        return (str == null || BLANK.equals(str)) ? true : false;
    }

    public static boolean isEmpty(CharSequence target) {
        return (target == null || isEmpty(target.toString())) ? true : false;
    }

    public static boolean isNull(String str) {
        return (str == null || BLANK.equals(str) || NULL.equals(str)) ? true : false;
    }

    public static boolean isNull(CharSequence target) {
        return (target == null || isNull(target.toString())) ? true : false;
    }

    public static boolean isValidEmail(CharSequence target) {
        return target == null || target.toString().contains("@");
    }

    public static boolean isValidPassword(CharSequence target) {
        return target != null && target.toString().length() > 4;
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }

    public static boolean checkIfDataIsUrlAndCreateIntent(String data) {
        boolean barcodeDataIsUrl;
        try {
            @SuppressWarnings("unused")
            URL url = new URL(data);
            barcodeDataIsUrl = true;
        }
        catch (MalformedURLException exc) {
            barcodeDataIsUrl = false;
        }
        return barcodeDataIsUrl;
    }

    public static String[] split(String data, String split) {
        return (data == null) ? null : data.split(split);
    }

    public static boolean contains(String data, String contain) {
        return (data == null) ? false : data.contains(contain);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static String formatNumber(long amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String generateRandomCode(int length) {
        if (mChBuffer == null || mChBuffer.length != length)
            mChBuffer = new char[length];
        for (int i = 0; i < mChBuffer.length; i ++) {
            mChBuffer[i] = symbols[mRandom.nextInt(symbols.length)];
        }
        return new String(mChBuffer);
    }

    public static String generateRandomNumber(int length) {
        if (mChBuffer == null || mChBuffer.length != length)
            mChBuffer = new char[length];
        for (int i = 0; i < mChBuffer.length; i ++) {
            mChBuffer[i] = symbolsNumber[mRandom.nextInt(symbolsNumber.length)];
        }
        return new String(mChBuffer);
    }

    public static float decodeFloat(String str)
    {
        if (StringHelper.isEmpty(str))
            return 0;
        if (str.contains(","))
        {
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            Number aaa = null;
            float ret;
            try {
                aaa = format.parse(str);
                ret = aaa.floatValue();
            } catch (ParseException e) {
                e.printStackTrace();
                ret = 0;
            }
            return ret;
        }
        else
        {
            return Float.parseFloat(str);
        }
    }

    public static String makeMoneyType(String moneyString) {
        String format = "#,###"/* "#.##0.00" */;
        DecimalFormat df = new DecimalFormat(format);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        dfs.setGroupingSeparator(',');
        df.setGroupingSize(3);
        df.setDecimalFormatSymbols(dfs);

        try {
            return (df.format(Float.parseFloat(moneyString))).toString();
        } catch (Exception e) {
            return moneyString;
        }
    }

    public static String encodeCurrencyThousand(float value)
    {
        return makeMoneyType(String.valueOf(value));
    }

    public static String encodeCurrency(float value)
    {
        String strResult = String.format("%.2f", value).replace(",", ".");
        return strResult;
    }

    public static String encodePercent(float value)
    {
        String strResult = String.format("%.1f", value).replace(",", ".");
        return strResult;
    }

    public static String convertToTitleCase(String input) {
        String[] strArray = input.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap;
            if (s.toLowerCase().equals("a.m."))
                cap = "AM";
            else if (s.toLowerCase().equals("p.m."))
                cap = "PM";
            else
                cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }

    public static String getThousandFormated(long value) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }



    public static int getRandomNumber(int min, int max){
        Random r = new Random();
        int random = r.nextInt(max - min + 1) + min;
        return random;
    }

    public static String parseName(String var0) {
        if (var0 == null) {
            return null;
        } else {
            int var1 = var0.lastIndexOf("@");
            return var1 <= 0 ? "" : var0.substring(0, var1);
        }
    }

    public static String parseServer(String var0) {
        if (var0 == null) {
            return null;
        } else {
            int var1 = var0.lastIndexOf("@");
            if (var1 + 1 > var0.length()) {
                return "";
            } else {
                int var2 = var0.indexOf("/");
                return var2 > 0 && var2 > var1 ? var0.substring(var1 + 1, var2) : var0.substring(var1 + 1);
            }
        }
    }

    public static String parseResource(String var0) {
        if (var0 == null) {
            return null;
        } else {
            int var1 = var0.indexOf("/");
            return var1 + 1 <= var0.length() && var1 >= 0 ? var0.substring(var1 + 1) : "";
        }
    }

    public static String parseBareAddress(String var0) {
        if (var0 == null) {
            return null;
        } else {
            int var1 = var0.indexOf("/");
            if (var1 < 0) {
                return var0;
            } else {
                return var1 == 0 ? "" : var0.substring(0, var1);
            }
        }
    }

    public static String getFrontSubString(String totalString, String str){
        String[] split = totalString.split(str);
        String firstSubString = split[0];
        return firstSubString;
    }

    public static String getDateRandomString(){
        Random r = new Random();
        int i1 = (r.nextInt(80) + 65);

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + i1 ;
        return timeStamp;
    }

}

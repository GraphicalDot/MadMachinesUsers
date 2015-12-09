package com.sports.unity.util;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sports.unity.XMPPManager.XMPPService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by amandeep on 15/10/15.
 */
public class CommonUtil {

    public static String getUserSimNumber(Context context) {
        String phoneNumber = null;

        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
            Log.i("Sports Unity", "Phone number not found through sim api");
        } else {
            phoneNumber = telephonyManager.getLine1Number();
        }

        return phoneNumber;
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


    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static long getCurrentGMTTimeInEpoch() {

        DateTime dateTimeInUtc = new DateTime(DateTime.now(), DateTimeZone.UTC);
        long epoch = (dateTimeInUtc.getMillis() / 1000);
        return epoch;
    }

    public static String getDefaultTimezoneTime(long gmtEpoch) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss zzz");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String time = String.valueOf(simpleDateFormat.format(gmtEpoch * 1000));
        return time;
    }

    public static String getDefaultTimezoneTimeInAMANDPM(long gmtEpoch) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("K:mm a");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String time = String.valueOf(simpleDateFormat.format(gmtEpoch * 1000));
        return time;
    }

    public static String getTimeDifference(long epochTime) {
        long currentTime = getCurrentGMTTimeInEpoch();
        DateTime dateTime = new DateTime(epochTime);
        DateTime dateTimenow = new DateTime(currentTime);
        int days = Days.daysBetween(dateTime, dateTimenow).getDays();
        if (days > 0) {
            return String.valueOf(days + " days");
        }

        return String.valueOf(0);
    }

//    public static int getStack(Context context) {
//        int numOfActivities = 0;
//        ActivityManager m = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
//        List<ActivityManager.AppTask> list = m.getAppTasks();
//        numOfActivities = list.size();
//
//        for (ActivityManager.AppTask taskInfo : list) {
//            Log.i("Activity Info", "" + taskInfo.getTaskInfo().origActivity.getClassName());
//        }
//
//        return numOfActivities;
//    }

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static String getMD5EncryptedString(byte[] content){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(content, 0, content.length);
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

}

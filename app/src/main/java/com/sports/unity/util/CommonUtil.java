package com.sports.unity.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

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

    public static int getStack(Context context) {
        int numOfActivities = 0;
        ActivityManager m = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> list = m.getAppTasks();
        numOfActivities = list.size();

        for(ActivityManager.AppTask taskInfo : list){
            Log.i( "Activity Info" , "" + taskInfo.getTaskInfo().origActivity.getClassName());
        }

        return numOfActivities;
    }

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

}

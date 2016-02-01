package com.sports.unity.util;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.XMPPManager.XMPPService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
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
            Log.i("Common", "Phone number not found through sim api");
        } else {
            phoneNumber = telephonyManager.getLine1Number();
            if (phoneNumber != null && phoneNumber.length() > 10) {
                phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
            } else {
                //nothing
            }
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

    public static String getTime(long gmtEpoch) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String time = String.valueOf(simpleDateFormat.format(gmtEpoch * 1000));
        return time;
    }

    public static String getDefaultTimezoneTimeInAMANDPM(long gmtEpoch) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String time = String.valueOf(simpleDateFormat.format(gmtEpoch * 1000));
        return time;
    }

    public static String getTimeDifference(long epochTime) {
        long currentTime = getCurrentGMTTimeInEpoch();
        DateTime dateTime = new DateTime(epochTime * 1000);
        DateTime dateTimenow = new DateTime(currentTime * 1000);
        int days = Days.daysBetween(dateTime, dateTimenow).getDays();
        if (days > 0) {
            return String.valueOf(days);
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

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static String getMD5EncryptedString(byte[] content) {
        String checksum = null;
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");

            mdEnc.update(content, 0, content.length);
            checksum = new BigInteger(1, mdEnc.digest()).toString(16);
            while (checksum.length() < 32) {
                checksum = "0" + checksum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return checksum;
    }

    public static String getMD5EncryptedString(Context context, String fileName) {
        String checksum = null;
        MessageDigest mdEnc = null;

        File file = new File(DBUtil.getFilePath(context, fileName));
        FileInputStream fileInputStream = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");

            fileInputStream = new FileInputStream(file);

            byte chunk[] = new byte[4096];
            int read = 0;
            while ((read = fileInputStream.read(chunk)) != -1) {
                mdEnc.update(chunk, 0, read);
            }

            checksum = new BigInteger(1, mdEnc.digest()).toString(16);
            while (checksum.length() < 32) {
                checksum = "0" + checksum;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception ex) {
            }
        }

        return checksum;
    }

}

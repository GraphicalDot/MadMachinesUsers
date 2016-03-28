package com.sports.unity.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sports.unity.BuildConfig;
import com.sports.unity.Database.DBUtil;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Contacts;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by amandeep on 15/10/15.
 */
public class CommonUtil {

//    public static String getUserSimNumber(Context context) {
//        String phoneNumber = null;
//
//        TelephonyManager telephonyManager;
//        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
//            Log.i("Common", "Phone number not found through sim api");
//        } else {
//            phoneNumber = telephonyManager.getLine1Number();
//            if (phoneNumber != null && phoneNumber.length() > 10) {
//                phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
//            } else {
//                //nothing
//            }
//        }
//        return phoneNumber;
//    }

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

    public static String getBuildConfig() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getDeviceId(Context context) {

        String id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return id;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(gmtEpoch * 1000);
        SimpleDateFormat simpleDateFormat;
        int a = cal.get(Calendar.AM_PM);
        if (a == Calendar.AM) {
            simpleDateFormat = new SimpleDateFormat("K:mm aa");
        } else {
            simpleDateFormat = new SimpleDateFormat("h:mm aa");
        }

        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String time = String.valueOf(simpleDateFormat.format(gmtEpoch * 1000));
        return time;
    }

    public static String getDeviceDetails() {
        String details = "\n" + "\n";
        details += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        details += "\n OS API Level: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.VERSION.SDK_INT + ")";
        details += "\n Device: " + android.os.Build.DEVICE;
        details += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
        details += "\n Brand : " + Build.BRAND;
        return details;
    }

    public static String getDefaultISOCountryCode(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String isoCode = manager.getSimCountryIso().toUpperCase();

        if (isoCode.isEmpty()) {
            isoCode = Locale.getDefault().getCountry();
        }

        return isoCode;
    }

    public static String getDefaultCountyCode(Context context) {
        String isoCountyCode = getDefaultISOCountryCode(context);
        String countryCode = getCountryDetailsByIsoCountryCode(context, isoCountyCode).get(0);
        return countryCode;
    }

    public static int getTimeDifference(long epochTime) {
        int diff = 0;

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis((getCurrentGMTTimeInEpoch()) * 1000);
        int currentDay = currentTime.get(Calendar.DAY_OF_YEAR);
        int currentYear = currentTime.get(Calendar.YEAR);


        Calendar messageTime = Calendar.getInstance();
        messageTime.setTimeInMillis((epochTime) * 1000);
        int messageDay = messageTime.get(Calendar.DAY_OF_YEAR);
        int messageYear = messageTime.get(Calendar.YEAR);

        if (currentYear - messageYear == 0) {
            diff = currentDay - messageDay;
        } else {
            int daysInYear = messageTime.getActualMaximum(Calendar.DAY_OF_YEAR);
            diff = (daysInYear - messageDay) + currentDay;
        }


        return diff;
    }

    public static void openSMSIntent(Contacts contact, Context context) {
        String inviteText = context.getString(R.string.download_sports_unity);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.putExtra("address", contact.jid);
//        intent.putExtra("sms_body", inviteText);
//        intent.setType("vnd.android-dir/mms-sms");
//        context.startActivity(intent);
        Uri uri = Uri.parse("smsto:" + contact.phoneNumber);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", inviteText);
        context.startActivity(it);
    }

    public static int getDrawable(String color, boolean toolbar) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Lollipop devices
            if (toolbar) {
                return R.drawable.image_view_selector;
            } else {
                return R.drawable.ripple_mask_drawable;
            }
        } else {
            // Lower than Lollipop
            if (color.equals(Constants.COLOR_WHITE)) {
                return R.drawable.layout_white_bg_selector;
            } else {
                return R.drawable.layout_blue_bg_selector;
            }
        }
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

    public static void openLinkOnBrowser(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
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

    public static String getMD5EncryptedString(Context context, String mimeType, String fileName) {
        String checksum = null;
        MessageDigest mdEnc = null;

        File file = new File(DBUtil.getFilePath(context, mimeType, fileName));
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

    public static ArrayList<String> getCountryDetailsByIsoCountryCode(Context context, String isoCountryCode) {
        ArrayList<String> countryDetails = new ArrayList<>();

        String[] countryList = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < countryList.length; i++) {
            String[] code = countryList[i].split(",");

            if (code[1].trim().equals(isoCountryCode.trim())) {
                countryDetails.add(code[0]);
                countryDetails.add(code[1]);
                countryDetails.add(code[2]);
                break;
            }
        }

        return countryDetails;
    }

    public static ArrayList<String> getCountryDetailsByCountryCode(Context context, String countryCode) {
        ArrayList<String> countryDetails = new ArrayList<>();

        String[] countryList = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < countryList.length; i++) {
            String[] code = countryList[i].split(",");

            if (code[0].trim().equals(countryCode.trim())) {
                countryDetails.add(code[0]);
                countryDetails.add(code[1]);
                countryDetails.add(code[2]);
                break;
            }
        }

        return countryDetails;
    }
    public static String getToken(Context context) throws IOException {
        InstanceID instanceID = InstanceID.getInstance(context);
        return instanceID.getToken(context.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
    }
}

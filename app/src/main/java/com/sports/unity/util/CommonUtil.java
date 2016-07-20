package com.sports.unity.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sports.unity.BuildConfig;
import com.sports.unity.ChatScreenApplication;
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
import java.net.URL;
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
        Uri uri = Uri.parse("smsto:+" + contact.phoneNumber);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Constants.REQUEST_PARAMETER_KEY_TOKEN, "");

    }

    public static int getDefaults(Context context, int defaults, NotificationCompat.Builder builder) {
        if (UserUtil.isConversationVibrate()) {
            defaults = defaults | Notification.DEFAULT_VIBRATE;
        } else {
            long vibratePattern[] = new long[]{0l};
            builder.setVibrate(vibratePattern);
        }

//        if ( soundEnabled(context) ) {
//            defaults = defaults | Notification.DEFAULT_SOUND;
//        } else {
//            builder.setSound(null);
//        }

        if (UserUtil.isNotificationLight()) {
            builder.setLights(context.getResources().getColor(R.color.app_theme_blue), 100, 3000);
        } else {

        }
        return defaults;
    }


    public static void sendAnalyticsData(Application application, String screenName) {
        /*Tracker mTracker = ((ChatScreenApplication) application).getDefaultTracker();
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
    }


    /**
     * Implementation of FireBase AppIndexing.
     * To start the app indexing a GoogleApiClient is required.
     * This methods returns a new GoogleApiClient.
     *
     * @param context - context of activity.
     * @return {@link GoogleApiClient} for AppIndexing.
     */
    public static GoogleApiClient getAppIndexingClient(Context context) {
        GoogleApiClient mClient = new GoogleApiClient.Builder(context).addApi(AppIndex.API).build();
        return mClient;
    }

    /**
     * Use the correct Action type for your content.
     * For example, use for opening static content and {@link Action#TYPE_WATCH} for playing video content.
     *
     * @param mTitle       - Title of the activity.
     * @param mDescription - Description of the activity.
     * @param mUrl         - DeepLink for this activity.
     * @return Action type for content.
     */
    public static Action getAction(String mTitle, String mDescription, Uri mUrl) {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(mUrl)
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**
     * Starts the AppIndexing.
     * <b>This must be called after <i>super.onStart()</i></b>
     *
     * @param mClient GoogleApiClient
     * @param action  Action type for your content.
     */
    public static void startAppIndexing(GoogleApiClient mClient, Action action) {
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, action);
    }

    /**
     * Stops the AppIndexing.
     * <b>This must be called before <i>super.onStop()</i></b>
     *
     * @param mClient GoogleApiClient
     * @param action  Action type for your content.
     */
    public static void stopAppIndexing(GoogleApiClient mClient, Action action) {
        AppIndex.AppIndexApi.end(mClient, action);
        mClient.disconnect();
    }

}

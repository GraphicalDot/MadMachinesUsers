package com.sports.unity.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.sports.unity.Database.DBUtil;

/**
 * Created by amandeep on 15/10/15.
 */
public class CommonUtil {

    public static String getUserSimNumber(Context context){
        String phoneNumber = null;

        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if ( telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT ) {
            Log.i( "Sports Unity", "Phone number not found through sim api");
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
}

package com.sports.unity.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

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

}

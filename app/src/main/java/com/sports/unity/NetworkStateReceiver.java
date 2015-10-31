package com.sports.unity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.util.CommonUtil;

/**
 * Created by madmachines on 16/10/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean connected = CommonUtil.isInternetConnectionAvailable(context);
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            XMPPClient.getInstance().internetStateChangeEvent( connected);
        } else {

        }

    }

}
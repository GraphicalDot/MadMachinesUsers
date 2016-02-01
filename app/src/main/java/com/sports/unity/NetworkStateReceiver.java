package com.sports.unity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.GlobalEventHandler;

/**
 * Created by madmachines on 16/10/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean connected = CommonUtil.isInternetConnectionAvailable(context);
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if( connected == true ) {
                XMPPService.startService(context);
            } else {
                //nothing
            }
        } else {
            //nothing
        }

        GlobalEventHandler.getInstance().internetStateChanged(connected);

    }

}
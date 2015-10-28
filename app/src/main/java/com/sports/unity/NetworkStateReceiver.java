package com.sports.unity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

/**
 * Created by madmachines on 16/10/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

        }
    }
}
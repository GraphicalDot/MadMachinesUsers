package com.sports.unity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by madmachines on 16/10/15.
 */
public class Connectivity {

    private static Connectivity connectivity = null;

    private Context context;

    private Connectivity(Context context) {
        this.context = context;
    }

    synchronized public static Connectivity getInstance(Context context) {
        if (connectivity == null) {
            connectivity = new Connectivity(context);
        }
        return connectivity;
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

}

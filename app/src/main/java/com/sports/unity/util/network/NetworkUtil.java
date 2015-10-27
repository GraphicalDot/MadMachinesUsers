package com.sports.unity.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.VolleyError;

/**
 * Created by Dell on 4/24/2015.
 */
public class NetworkUtil {

    public static boolean isOnline( Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void showNoNetworkToast( Context context){
//        Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
    }

    public static void showProblemToast( Context context, VolleyError error){
        Toast.makeText( context, "Problem : "+error.getMessage(), Toast.LENGTH_SHORT).show();
    }

}

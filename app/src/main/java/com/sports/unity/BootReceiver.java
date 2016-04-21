package com.sports.unity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sports.unity.XMPPManager.XMPPService;

/**
 * Created by amandeep on 14/11/15.
 */
public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        XMPPService.startService(context);
    }

}

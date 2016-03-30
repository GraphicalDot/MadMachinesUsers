package com.sports.unity.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by madmachines on 17/3/16.
 */
public class SUGcmInstanceIdListener extends InstanceIDListenerService
{
    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}

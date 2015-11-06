package com.sports.unity.common.controller;

import android.support.v7.app.AppCompatActivity;

import com.sports.unity.XMPPManager.XMPPClient;

/**
 * Created by madmachines on 3/11/15.
 */
public class CustomAppCompatActivity extends AppCompatActivity {

    private static int activityCounter = 0;

    @Override
    protected void onStart() {
        super.onStart();
        activityCounter++;

        if (activityCounter == 1) {
            XMPPClient.sendOnlinePresence();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityCounter--;

        if (activityCounter == 0) {
            XMPPClient.sendOfflinePresence();
        }

    }


}

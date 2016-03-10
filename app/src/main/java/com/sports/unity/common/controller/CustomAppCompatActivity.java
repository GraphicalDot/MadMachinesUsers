package com.sports.unity.common.controller;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.GlobalEventHandler;
import com.sports.unity.util.GlobalEventListener;

import org.jivesoftware.smackx.ping.PingManager;

/**
 * Created by madmachines on 3/11/15.
 */
public class CustomAppCompatActivity extends AppCompatActivity implements GlobalEventListener {

    private static int activityCounter = 0;
    private String NETWORK_STATE_ACTIVITY_TAG = "network_state_activity_tag";
    private PingManager pingManager;
    private boolean isPingRequired;

    @Override
    protected void onStart() {
        super.onStart();
        activityCounter++;
        if (activityCounter == 1) {
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                pingManager = PingManager.getInstanceFor(XMPPClient.getConnection());
                pingManager.setPingInterval(10);
                pingManager.pingServerIfNecessary();
                isPingRequired = true;
            } else {
                isPingRequired = false;
            }
            GlobalEventHandler.getInstance().addGlobalEventListener(NETWORK_STATE_ACTIVITY_TAG, this);
            PersonalMessaging.getInstance(this).sendOnlinePresence();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityCounter--;
        if (activityCounter == 0) {
            PersonalMessaging.getInstance(this).sendOfflinePresence();
            GlobalEventHandler.getInstance().removeGlobalEventListener(NETWORK_STATE_ACTIVITY_TAG);
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                if (isPingRequired) {
                    pingManager = PingManager.getInstanceFor(XMPPClient.getConnection());
                    pingManager.setPingInterval(-10);
                    isPingRequired=false;
                }
            }
        }
    }

    public static boolean isActivityCounterNull() {
        if (activityCounter == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onInternetStateChanged(boolean connected) {

    }

    @Override
    public void onXMPPServiceAuthenticated(boolean connected) {
        if (connected) {
            if (activityCounter == 1) {
                PersonalMessaging.getInstance(this).sendOnlinePresence();
                if (!isPingRequired) {
                    pingManager = PingManager.getInstanceFor(XMPPClient.getConnection());
                    pingManager.setPingInterval(10);
                    pingManager.pingServerIfNecessary();
                    isPingRequired = true;
                }

            } else if (activityCounter == 0) {
                PersonalMessaging.getInstance(this).sendOfflinePresence();
                if (isPingRequired) {
                    if (pingManager != null) {
                        pingManager.setPingInterval(-10);
                        isPingRequired = false;
                    }
                }
            }
        } else {
            //nothing
        }
    }
}

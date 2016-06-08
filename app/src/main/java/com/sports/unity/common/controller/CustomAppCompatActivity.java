package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.appevents.AppEventsLogger;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.GlobalEventHandler;
import com.sports.unity.util.GlobalEventListener;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by madmachines on 3/11/15.
 */
public class CustomAppCompatActivity extends AppCompatActivity implements GlobalEventListener {

    private static int activityCounter = 0;
    private String NETWORK_STATE_ACTIVITY_TAG = "network_state_activity_tag";

//    private PingManager pingManager;
//    private boolean isPingRequired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserUtil.init(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        GlobalEventHandler.getInstance().addGlobalEventListener(NETWORK_STATE_ACTIVITY_TAG, this);

        activityCounter++;
        if (activityCounter == 1) {
            /*if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                pingManager = PingManager.getInstanceFor(XMPPClient.getConnection());
                pingManager.setPingInterval(10);
                pingManager.pingServerIfNecessary();
                isPingRequired = true;
            } else {
                isPingRequired = false;
            }*/
            PersonalMessaging.getInstance(this).sendOnlinePresence();
            AppEventsLogger.activateApp(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityCounter--;
        if (activityCounter == 0) {
            PersonalMessaging.getInstance(this).sendOfflinePresence();
            GlobalEventHandler.getInstance().removeGlobalEventListener(NETWORK_STATE_ACTIVITY_TAG);
            AppEventsLogger.deactivateApp(this);
            /*if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                if (isPingRequired) {
                    pingManager = PingManager.getInstanceFor(XMPPClient.getConnection());
                    pingManager.setPingInterval(-10);
                    isPingRequired=false;
                }
            }*/
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
    public void onXMPPServiceAuthenticated(boolean connected, XMPPConnection connection) {
        if (connected) {
            if (activityCounter == 1) {
                PersonalMessaging.getInstance(this).sendOnlinePresence();
                /*if (!isPingRequired) {
                    pingManager = PingManager.getInstanceFor(XMPPClient.getConnection());
                    pingManager.setPingInterval(10);
                    pingManager.pingServerIfNecessary();
                    isPingRequired = true;
                }*/

            } else if (activityCounter == 0) {
                PersonalMessaging.getInstance(this).sendOfflinePresence();
                /*if (isPingRequired) {
                    if (pingManager != null) {
                        pingManager.setPingInterval(-10);
                        isPingRequired = false;
                    }
                }*/
            }
        } else {
            //nothing
        }
    }

    @Override
    public void onReconnecting(int seconds) {

    }

    @Override
    public void onConnectionReplaced(Exception e) {

    }

}

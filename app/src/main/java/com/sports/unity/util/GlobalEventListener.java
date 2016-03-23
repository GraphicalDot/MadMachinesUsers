package com.sports.unity.util;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by amandeep on 12/1/16.
 */
public interface GlobalEventListener {

    public void onInternetStateChanged(boolean connected);

    public void onXMPPServiceAuthenticated(boolean connected, XMPPConnection connection);

    public void onReconnecting(int seconds);
}

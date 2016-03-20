package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public interface XMPPConnectionListener{

    public void onSuccessfulConnection(XMPPTCPConnection connection);

    public void onConnectionLost();
}
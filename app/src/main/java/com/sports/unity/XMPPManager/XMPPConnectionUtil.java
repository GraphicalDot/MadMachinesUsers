package com.sports.unity.XMPPManager;

import android.content.Context;
import android.util.Log;

import com.sports.unity.util.GlobalEventHandler;
import com.sports.unity.util.GlobalEventListener;

import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Mad on 3/14/2016.
 */
public class XMPPConnectionUtil {

    private static XMPPConnectionUtil CONNECTION_UTIL;

    synchronized public static XMPPConnectionUtil getInstance() {
        if (CONNECTION_UTIL == null) {
            CONNECTION_UTIL = new XMPPConnectionUtil();
        }
        return CONNECTION_UTIL;
    }

    private HashMap<String, XMPPConnectionListener> connectionMap = new HashMap<>();

    private XMPPConnectionUtil() {

    }

    public void requestConnection(Context context) {
        if ( ! XMPPClient.getInstance().isConnectionAuthenticated() ) {
            XMPPService.startService(context);
        } else {
            GlobalEventHandler.getInstance().xmppServerConnected(true, XMPPClient.getConnection());
        }
    }

}

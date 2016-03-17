package com.sports.unity.XMPPManager;

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
public class XMPPConnectionUtil implements GlobalEventListener {

    private static final String GLOBAL_EVENT_KEY = "global_event_key_for_connection";

    private Thread connectionThread;
    private static XMPPConnectionUtil CONNECTION_UTIL;

    private XMPPConnectionListener connectionListener;

    private HashMap<String, XMPPConnectionListener> connectionMap;


    private XMPPConnectionUtil() {
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPService service = XMPPService.getXMPP_SERVICE();
                if (service != null) {
                    XMPPClient.getInstance().reconnectConnection(service.getConnectionListener());
                } else {
                    //nothing
                }
            }
        });
        connectionMap = new HashMap<String, XMPPConnectionListener>();
    }

    public static XMPPConnectionUtil getInstance() {
        if (CONNECTION_UTIL == null) {
            CONNECTION_UTIL = new XMPPConnectionUtil();
        }
        return CONNECTION_UTIL;
    }

    public void requestConnection() {
        if (!XMPPClient.getInstance().isConnectionAuthenticated() && !connectionThread.isAlive()) {
            connectionThread.start();
        }
    }

    public void addConnectionListener(String key, XMPPConnectionListener listener) {
        if (connectionMap.size() == 0) {
            GlobalEventHandler.getInstance().addGlobalEventListener(GLOBAL_EVENT_KEY, this);
        }
        if (!connectionMap.containsKey(key)) {
            connectionMap.put(key, listener);
        }
    }

    public void removeConnectionListener(String key) {
        if (connectionMap.containsKey(key)) {
            connectionMap.remove(key);
        }
        if (connectionMap.size() == 0) {
            GlobalEventHandler.getInstance().removeGlobalEventListener(GLOBAL_EVENT_KEY);
        }
    }

    @Override
    public void onInternetStateChanged(boolean connected) {

    }

    @Override
    public void onXMPPServiceAuthenticated(boolean connected, XMPPConnection connection) {
        if (connected) {
            Log.d("dmax", "Connected");
            Iterator<String> iterator = connectionMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                XMPPConnectionListener listener = connectionMap.get(key);
                if (listener != null) {
                    listener.onSuccessfulConnection(XMPPClient.getConnection());
                }
            }
        } else {
            Log.d("dmax", "Connection lost");
            Iterator<String> iterator = connectionMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                XMPPConnectionListener listener = connectionMap.get(key);
                if (listener != null) {
                    listener.onConnectionLost();
                }
            }
        }
    }

    @Override
    public void onReconnecting(int seconds) {
        Log.d("dmax", "Reconnecting in" + seconds);
    }
}

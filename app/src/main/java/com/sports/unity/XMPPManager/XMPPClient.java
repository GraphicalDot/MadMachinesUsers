package com.sports.unity.XMPPManager;

import android.content.Context;
import android.util.Log;

import com.sports.unity.ConnectivityListener;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by madmachines on 31/8/15.
 */
public class XMPPClient implements ConnectivityListener {

    public static String SERVER_HOST = "54.169.217.88";
    public static int SERVER_PORT = 5222;
    public static String SERVICE_NAME = "mm.io";

    public static XMPPTCPConnection connection = null;

    private static XMPPClient XMPP_CLIENT = null;

    public static XMPPClient getInstance() {
        if (XMPP_CLIENT == null) {
            XMPP_CLIENT = new XMPPClient();
        }
        return XMPP_CLIENT;
    }

//    public static void reconnectAndAuthenticate_OnThread(final Context context){
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                boolean success = reconnectConnection();
//                if( success ) {
//                    authenticateConnection(context);
//                }
//            }
//
//        });
//        thread.start();
//    }

    synchronized public static boolean reconnectConnection() {
        boolean success = false;

        if (connection == null) {
            success = openConnection();
            if (success) {
                XMPPService.getXmppService().attachListeners(connection);
            } else {
                //nothing
            }
        } else {
            if (!connection.isConnected()) {
                try {
                    connection.connect();
                    XMPPService.getXmppService().attachListeners(connection);

                    success = true;
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            } else {
                success = true;
            }
        }

        return success;
    }

    synchronized public static boolean authenticateConnection(Context context) {
        boolean success = false;

        if (connection != null) {
            if (!connection.isAuthenticated()) {
                try {
                    TinyDB tinyDB = TinyDB.getInstance(context);
                    connection.login(tinyDB.getString(TinyDB.KEY_USERNAME), tinyDB.getString(TinyDB.KEY_PASSWORD));

                    success = true;
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                success = true;
            }
        } else {
            //nothing
        }

        return success;
    }

    public static XMPPTCPConnection getConnection() {
        if (connection == null) {
            reconnectConnection();
        }
        return connection;
    }

    public static void sendOfflinePresence() {
        if (connection != null) {
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("Offline");
            try {
                connection.sendPacket(presence);
                Log.i("custompresence", "offline");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

            Message message = new Message("settimedev@mm.io", Message.Type.headline);
            DateTime dateTime = DateTime.now();
            message.setBody(String.valueOf(dateTime.getMillis()));
            try {
                connection.sendPacket(message);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        } else {
            //TODO
        }
    }

    public static void sendOnlinePresence() {
        if (connection != null) {
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("Online");
            try {
                connection.sendPacket(presence);
                Log.i("custompresence", "online");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized private static boolean openConnection() {
        boolean success = false;

        try {
            if (null == connection) {
                XMPPTCPConnectionConfiguration.Builder configuration = XMPPTCPConnectionConfiguration.builder();
                configuration.setHost(SERVER_HOST);
                configuration.setPort(SERVER_PORT);
                configuration.setServiceName(SERVICE_NAME);
                configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
                configuration.setDebuggerEnabled(true);
                configuration.setConnectTimeout(20000);
                configuration.setSendPresence(true);

                connection = new XMPPTCPConnection(configuration.build());
                connection.setUseStreamManagement(true);
                connection.setUseStreamManagementResumption(true);

                XMPPTCPConnection.setUseStreamManagementDefault(true);
                XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);

                ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                reconnectionManager.enableAutomaticReconnection();
                reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);

                Boolean k = reconnectionManager.isAutomaticReconnectEnabled();
                Log.i("R Manager : ", k ? "yeah" : "no");

                connection.connect();
            }

            success = true;
        } catch (XMPPException xe) {
            xe.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    private static void closeConnection() {
        connection.disconnect();
        connection = null;
    }

    private XMPPClient() {

    }

    @Override
    public void internetStateChangeEvent(final Context context, boolean state) {
        if (state == true) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    reconnectConnection();
                }

            });
            thread.start();
        } else {
            //nothing
        }
    }

}

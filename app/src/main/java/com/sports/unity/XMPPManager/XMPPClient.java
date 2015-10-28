package com.sports.unity.XMPPManager;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by madmachines on 31/8/15.
 */
public class XMPPClient {

    public static String SERVER_HOST = "54.169.217.88";
    public static int SERVER_PORT = 5222;
    public static String SERVICE_NAME = "mm.io";
    public static XMPPTCPConnection connection = null;
    public static ReconnectionManager reconnectionManager;


    private static void openConnection() {
        try {
            if (null == connection || !connection.isAuthenticated()) {
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

                reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                reconnectionManager.enableAutomaticReconnection();
                reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);

                Boolean k = reconnectionManager.isAutomaticReconnectEnabled();
                Log.i("R Manager : ", k ? "yeah" : "no");

                connection.connect();

            }
        } catch (XMPPException xe) {
            xe.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static XMPPTCPConnection getConnection() {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    public static void closeConnection() {
        connection.disconnect();
        connection = null;

    }


}

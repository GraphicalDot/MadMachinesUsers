package com.sports.unity.XMPPManager;

import android.content.Context;
import android.util.Log;

import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * Created by madmachines on 31/8/15.
 */
public class XMPPClient {

    public static String SERVER_HOST = "52.74.142.219"; //staging server
//    public static String SERVER_HOST = "54.169.217.88"; //production server
//    public static String SERVER_HOST = "192.168.1.143"; //local server
    public static int SERVER_PORT = 5222;
    public static String SERVICE_NAME = "mm.io";

    private static XMPPClient XMPP_CLIENT = null;

    public static XMPPClient getInstance() {
        if (XMPP_CLIENT == null) {
            XMPP_CLIENT = new XMPPClient();
        }
        return XMPP_CLIENT;
    }

    public static XMPPTCPConnection getConnection() {
        XMPPClient xmppClient = getInstance();
        if (xmppClient.connection == null) {
            XMPPService service = XMPPService.getXMPP_SERVICE();
            if (service != null) {
                XMPPClient.getInstance().reconnectConnection(service.getConnectionListener());
            } else {
                //nothing
            }
        }
        return xmppClient.connection;
    }

    private XMPPTCPConnection connection = null;

    private boolean chatRelatedListenersAdded = true;

    public boolean isChatRelatedListenersAdded() {
        return chatRelatedListenersAdded;
    }

    public void setChatRelatedListenersAdded(boolean chatRelatedListenersAdded) {
        this.chatRelatedListenersAdded = chatRelatedListenersAdded;
    }

    synchronized public boolean reconnectConnection() {
        boolean success = false;

        XMPPService xmppService = XMPPService.getXMPP_SERVICE();
        if (xmppService != null) {
            ConnectionListener connectionListener = xmppService.getConnectionListener();
            success = XMPPClient.getInstance().reconnectConnection(connectionListener);
        } else {
            //nothing
        }

        return success;
    }

    synchronized public boolean reconnectConnection(ConnectionListener connectionListener) {
        boolean success = false;

        if (connection == null) {
            success = openConnection(connectionListener);
        } else {
            if (!connection.isConnected()) {
                try {
                    connection.connect();

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

        addCustomExtensions();

        if (success) {
            ReadReceiptManager.getInstanceFor(connection);
        } else {

        }

        return success;
    }

    public boolean isConnectionAuthenticated() {
        boolean authenticated = false;
        if (connection != null && connection.isAuthenticated()) {
            authenticated = true;
        } else {
            //nothing
        }
        return authenticated;
    }

    synchronized public boolean authenticateConnection(Context context) {
        boolean success = false;
        TinyDB tinyDB = TinyDB.getInstance(context);
        String jid = tinyDB.getString(TinyDB.KEY_USER_JID);
        String password = tinyDB.getString(TinyDB.KEY_PASSWORD);

        if (connection != null) {
            if (!connection.isAuthenticated()) {
                try {
                    Log.i("XMPP Connection", "authenticating");
                    connection.login(jid, password);

                    success = true;

                    //PubSubMessaging.getInstance(context).getJoinedGroups(context);
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

    synchronized private boolean openConnection(ConnectionListener connectionListener) {
        boolean success = false;

        try {
            if (null == connection) {
                Log.i("XMPP Connection", "making new connection");

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

                connection.setPacketReplyTimeout(20000);

                Log.i("XMPP Connection", "adding connection listener");
                connection.addConnectionListener(connectionListener);
                chatRelatedListenersAdded = false;

                XMPPTCPConnection.setUseStreamManagementDefault(true);
                XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);

                ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                reconnectionManager.enableAutomaticReconnection();
                reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);

            } else {
                //nothing
            }

            if (!connection.isConnected()) {
                connection.connect();
            } else {
                //nothing
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

    public boolean sendReadStatus(String to, String messagePacketID) {
        boolean success = false;
        try {
            if (connection != null && connection.isAuthenticated()) {
                Message message = new Message(to);
                ReadReceipt read = new ReadReceipt(messagePacketID);
                message.addExtension(read);

                connection.sendPacket(message);

                success = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private void addCustomExtensions() {
        ProviderManager.addExtensionProvider(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE, new ReadReceipt.Provider());
    }

    private void closeConnection() {
        connection.disconnect();
        connection = null;
    }

    private XMPPClient() {

    }

}

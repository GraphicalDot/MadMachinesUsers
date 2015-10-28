package com.sports.unity.XMPPManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.PersonalMessaging;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class XMPPService extends Service {
    private XMPPTCPConnection mConnection = null;
    int mNotificationId = 1;
    private SportsUnityDBHelper sportsUnityDBHelper;
    private TinyDB tinyDB;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onHandleIntent(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.i("Onstcmd : ", "called");
        return START_STICKY;
    }

    protected void onHandleIntent(Intent intent) throws IOException, XMPPException, SmackException {
        if (mConnection == null) {
            mConnection = ProfileCreationActivity.returnConnection();
            if (!mConnection.isAuthenticated())
                mConnection.login(tinyDB.getString(TinyDB.KEY_USERNAME), tinyDB.getString(TinyDB.KEY_PASSWORD));

            attachListeners(mConnection);
        }
    }

    private void attachListeners(final XMPPTCPConnection connection) {

        /**
         * Attach a listener to listen if xmpp connection from the server breaks
         */

        connection.addConnectionListener(new XmppConnectionListener());

        /**
         * Make a filter for message packets
         */

        StanzaFilter filter = new StanzaTypeFilter(Message.class);
        /**
         * attach a packet listener to listen for incoming messages packets and status packet(active,typing etc)
         */

        connection.addSyncStanzaListener(new ChatMessageListener(), filter);

        /**
         * Attach a group invitation listener to connection variable to listen for incoming connections
         */

        MultiUserChatManager.getInstanceFor(connection).addInvitationListener(new InvitationListener() {
            @Override
            public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
                try {
                    room.join(TinyDB.getInstance(getApplicationContext()).getString("facebookname"));
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * Listener for acknowledging recieved receipts
         */

        DeliveryReceiptManager.getInstanceFor(XMPPClient.getConnection()).addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                /**
                 * fromJid : the person who received our message
                 * toJid : our phone number(our id)
                 * receiptId : id of the message that has reached the other person
                 */
                PersonalMessaging.getInstance(getApplicationContext()).setReceivedReceipts(fromJid, receiptId, getApplicationContext());
            }
        });

        /**
         * Listen for subscription packets to read status
         */

        connection.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                Presence presence = (Presence) packet;
                if (presence.getType() == Presence.Type.subscribe) {
                    Presence response = new Presence(Presence.Type.subscribed);
                    response.setTo(presence.getFrom().substring(0, presence.getFrom().indexOf("@")));
                    connection.sendPacket(response);
                }
            }
        }, new PacketTypeFilter(Presence.class));

    }

    private class XmppConnectionListener implements ConnectionListener {


        @Override
        public void connected(XMPPConnection connection) {

        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {

        }

        @Override
        public void connectionClosed() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mConnection = ProfileCreationActivity.returnConnection();
                }
            });
            Log.i("connection", "closed");

        }

        @Override
        public void connectionClosedOnError(Exception e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mConnection = ProfileCreationActivity.returnConnection();
                }
            });
            Log.i("connection", "closed on error");

        }

        @Override
        public void reconnectionSuccessful() {
            if (mConnection.isAuthenticated()) {
                Log.i("isAuthenticated", String.valueOf(XMPPClient.getConnection().isAuthenticated()));
            } else {
                try {
                    mConnection.login(tinyDB.getString(TinyDB.KEY_USERNAME), tinyDB.getString(TinyDB.KEY_PASSWORD));
                    Log.i("isAuthenticated", String.valueOf(XMPPClient.getConnection().isAuthenticated()));
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("reconnection", "succesful");

        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.i("reconnectingIn", String.valueOf(seconds));
        }

        @Override
        public void reconnectionFailed(Exception e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mConnection = ProfileCreationActivity.returnConnection();
                }
            });
            Log.i("reconnection", "failed");

        }
    }


    private class ChatMessageListener implements StanzaListener {

        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            Message message = (Message) packet;
            if (message.getType().equals(Message.Type.chat)) {
                if (message.getBody() == null) {
                    getStatus(message);
                } else {
                    handleChatMessage(message);
                }
            } else if (message.getType().equals(Message.Type.groupchat)) {
            } else if (((Message) packet).getType().equals(Presence.Type.subscribe)) {
                Log.i("subscription", "recieved");
            }
        }
    }

    private void handleChatMessage(Message message) {

        Object value = JivePropertiesManager.getProperty(message, "time");
        Object isGroupChat = JivePropertiesManager.getProperty(message, "isGroupChat");
        addToDatabase(message, value, isGroupChat);
        if (ChatScreenApplication.isActivityVisible()) {
            if (ChatScreenActivity.getJABBERID().equals(message.getFrom().toString().substring(0, message.getFrom().indexOf("@")))) {
                Intent intent = new Intent();
                intent.setAction("com.madmachines.SINGLE_MESSAGE_RECEIVED");
                sendBroadcast(intent);
                Log.i("Broadcast : ", "sent");
            } else {
                try {
                    DisplayNotification(message);
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                DisplayNotification(message);
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        Log.i("personalmessage :", "recieved");
    }

    private void getStatus(Message message) {
        Log.i("getting status :", "true");
        if (message.hasExtension(ChatState.composing.toString(), ChatStateExtension.NAMESPACE)) {

            Log.i("status :", "composing");

        } else if (message.hasExtension(ChatState.active.toString(), ChatStateExtension.NAMESPACE)) {

            Log.i("status :", "active");

        } else if (message.hasExtension(ChatState.gone.toString(), ChatStateExtension.NAMESPACE)) {

            Log.i("status :", "gone");

        } else if (message.hasExtension(ChatState.paused.toString(), ChatStateExtension.NAMESPACE)) {

            Log.i("status :", "paused");

        } else if (message.hasExtension(ChatState.inactive.toString(), ChatStateExtension.NAMESPACE)) {

            Object value = JivePropertiesManager.getProperty(message, "time");
            DateTime dateTime = new DateTime(value);
            SimpleDateFormat formatter = new SimpleDateFormat("k:mm");
            //sportsUnityDBHelper.getInstance(getApplicationContext()).updateStatus(String.valueOf(formatter.format(dateTime.getMillis()), getChatId()));
            Log.i("status :", "inactive");

        }
    }

    @Override
    public void onCreate() {
        Log.i("service", "created");
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
        tinyDB = TinyDB.getInstance(this);
        //getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, ContactsObserver.getInstance(new Handler(), this));
        /*IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkStateReceiver(), filter);*/
    }

    public void addToDatabase(Message message, Object value, Object isGroupChat) {

        String from = message.getFrom().toString().substring(0, message.getFrom().indexOf("@"));

        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
        long groupChatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
        boolean isGroup = false;

        if ("F".equals(String.valueOf(isGroupChat))) {
            SportsUnityDBHelper.Contacts contact = sportsUnityDBHelper.getContact(from);

            chatId = sportsUnityDBHelper.getChatEntryID(contact.id);
            if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
                //nothing
            } else {
                chatId = sportsUnityDBHelper.createChatEntry(contact.name, contact.id);
                Log.i("ChatEntry : ", "chat entry made from server " + chatId + " , " + contact.id);
            }

        } else {
            //TODO handle for group chat
        }

        DateTime dateTime = new DateTime(value);
        SimpleDateFormat formatter = new SimpleDateFormat("k:mm");
        Log.i("senttime : ", String.valueOf(formatter.format(dateTime.getMillis())));
        Log.i("Message Entry", "adding message from server chat " + chatId);
        sportsUnityDBHelper.addTextMessage(message.getBody().toString(), from, false,
                String.valueOf(formatter.format(dateTime.getMillis())), message.getStanzaId(), null, null,
                chatId, groupChatId, isGroup);

    }


    public void DisplayNotification(Message message) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

        String number = message.getFrom().substring(0, message.getFrom().indexOf("@"));
        String name = sportsUnityDBHelper.getJabberName(number);
        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
        notificationIntent.putExtra("jid", number);
        notificationIntent.putExtra("jbname", name);
        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, mNotificationId, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setContentText(message.getBody().toString());
        if (name != null && !name.isEmpty())
            builder.setContentTitle(name);
        else {
            /*builder.setContentTitle(jabberID);
            VCard card = new VCard();
            card.load(XMPPClient.getConnection(), jabberID + "@mm.io");
            sportsUnityDBHelper.addToContacts(jabberID, jabberID, true, true, card.getMiddleName());*/

        }
        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, builder.build());
        sportsUnityDBHelper.updateUnreadCount(sportsUnityDBHelper.getContactId(number));
        Intent intent = new Intent();
        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        mConnection.disconnect();
        Log.i("service :", "destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}

package com.sports.unity.XMPPManager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.ContactsObserver;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.GlobalEventHandler;
import com.sports.unity.util.GlobalEventListener;
import com.sports.unity.util.NotificationHandler;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.IQResultReplyFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jivesoftware.smackx.pubsub.EventElementType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.pubsub.listener.NodeConfigListener;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

public class XMPPService extends Service {

//    public static UserSearchManager searchManager;
//    public static Form searchForm = null;
//    public static Form answerForm = null;

    private static XMPPService XMPP_SERVICE = null;

    public static XMPPService getXMPP_SERVICE() {
        return XMPP_SERVICE;
    }

    public static void startService(Context context) {
        if (!isMyServiceRunning(XMPPService.class, context)) {
            Intent serviceIntent = new Intent(context, XMPPService.class);
            context.startService(serviceIntent);
        } else {
            Log.i("service", "running");
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    XMPPService service = XMPPService.getXMPP_SERVICE();
                    if (service != null) {
                        XMPPClient.getInstance().reconnectConnection(service.connectionListener);
                    } else {
                        //nothing
                    }
                }

            });
            thread.start();
        }
    }

    public static void displayNotification(Context context, String message, String from, String mimeType, int chatId, boolean isGroupChat, String groupServerId, byte[] image, int availabilityStatus, String userStatus) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        UserUtil.init(context);

        String name = sportsUnityDBHelper.getUserNameByJid(from);
        if (isGroupChat) {
            name = name + "@" + sportsUnityDBHelper.getGroupSubject(groupServerId);
            availabilityStatus = Contacts.AVAILABLE_BY_MY_CONTACTS;
        } else {
            //nothing
        }

        NotificationHandler notificationHandler = NotificationHandler.getInstance(context);
        notificationHandler.addNotificationMessage(chatId, name, message, mimeType, image, availabilityStatus);

        int chatCount = notificationHandler.getNotificationChatCount();
        PendingIntent pendingIntent = null;
        if (chatCount > 1) {
            pendingIntent = getPendingIntentForMainActivity(context);
        } else if (chatCount == 1) {
            if (isGroupChat) {
                String groupName = groupServerId.substring(groupServerId.indexOf("%") + 1, groupServerId.indexOf("%%"));
                pendingIntent = getPendingIntentForChatActivity(context, isGroupChat, groupName, groupServerId, chatId, null, false, availabilityStatus, userStatus);
            } else {
                Contacts contact = sportsUnityDBHelper.getContactByJid(from);
                pendingIntent = getPendingIntentForChatActivity(context, isGroupChat, name, from, chatId, contact.image, contact.isOthers(), availabilityStatus, userStatus);
            }
        }

        notificationHandler.showNotification(context, pendingIntent);
        ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.UNREAD_COUNT_KEY);
    }

    public static int getChatIdOrCreateIfNotExist(Context context, boolean isGroupChat, String from, boolean nearByChat) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        int chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

        if (!isGroupChat) {
            Contacts contact = sportsUnityDBHelper.getContactByJid(from);
            if (contact == null) {
                createContact(from, context, nearByChat);
                contact = sportsUnityDBHelper.getContactByJid(from);
            }
            chatId = contact.id;
        } else {
            chatId = sportsUnityDBHelper.getChatEntryID(from);
        }
        return chatId;
    }

    public static boolean createContact(String jid, Context context, boolean nearByChat) {
        boolean success = false;
        try {
            //TODO add contact in db first then try to load, and if not able to load vcard, atleast contact created for sure.

            XMPPTCPConnection connection = XMPPClient.getInstance().getConnection();
            VCard card = new VCard();
            card.load(connection, jid + "@mm.io");
            String status = card.getMiddleName();
            byte[] image = card.getAvatar();
            String nickname = card.getNickName();

            if (nearByChat) {
                SportsUnityDBHelper.getInstance(context).addToContacts(nickname, null, jid, ContactsHandler.getInstance().defaultStatus, null, Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME);
            } else {
                SportsUnityDBHelper.getInstance(context).addToContacts(nickname, null, jid, ContactsHandler.getInstance().defaultStatus, null, Contacts.AVAILABLE_BY_OTHER_CONTACTS);
            }
            SportsUnityDBHelper.getInstance(context).updateContacts(jid, image, status);

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static PendingIntent getPendingIntentForMainActivity(Context context) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("tab_index", 2);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(context, NotificationHandler.NOTIFICATION_ID, new Intent[]{mainIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private static PendingIntent getPendingIntentForChatActivity(Context context, boolean isGroupChat, String name, String from, int chatId, byte[] contactImage, boolean isOtherChat, int availabilityStatus, String userStatus) {
        Intent notificationIntent = ChatScreenActivity.createChatScreenIntent(context, isGroupChat, from, name, chatId, contactImage, false, isOtherChat, availabilityStatus, userStatus);

        Intent backIntent = new Intent(context, MainActivity.class);
        backIntent.putExtra("tab_index", 2);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(context, NotificationHandler.NOTIFICATION_ID, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public static PendingIntent getPendingIntentForScoreDetailActivity(Context context, Intent scoreDetailIntent) {
        Intent backIntent = new Intent(context, MainActivity.class);
        backIntent.putExtra("tab_index", 0);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(context, NotificationHandler.NOTIFICATION_ID, new Intent[]{backIntent, scoreDetailIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private SportsUnityDBHelper sportsUnityDBHelper = null;
    private XmppConnectionListener connectionListener = null;

    @Override
    public void onCreate() {
        Log.i("Service", "created");

        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, ContactsObserver.getInstance(new Handler(), this));

        XMPP_SERVICE = this;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        startService(getApplicationContext());
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion == Build.VERSION_CODES.KITKAT) {
            Intent restartService = new Intent(getApplicationContext(),
                    XMPPService.class);
            restartService.setPackage(getPackageName());
            PendingIntent restartServicePI = PendingIntent.getService(
                    getApplicationContext(), 1, restartService,
                    PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
        }

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i("Service", "started");

        new Thread(new Runnable() {

            @Override
            public void run() {

                sportsUnityDBHelper = SportsUnityDBHelper.getInstance(XMPPService.this);

                sportsUnityDBHelper.addDummyMessageIfNotExist();
                sportsUnityDBHelper.addDummyContactIfNotExist();

                UserUtil.init(getApplicationContext());

                connectionListener = new XmppConnectionListener();

                XMPPClient.getInstance().reconnectConnection(connectionListener);
            }

        }).start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public XmppConnectionListener getConnectionListener() {
        return connectionListener;
    }

    private void attachChatRelatedListeners(final XMPPTCPConnection connection) {
        XMPPClient xmppClient = XMPPClient.getInstance();

        if (xmppClient.isChatRelatedListenersAdded() == false) {
            xmppClient.setChatRelatedListenersAdded(true);

            Log.i("XMPP Connection", "attaching chat related listeners");


            /**
             * attach a packet listener to listen for incoming messages packets and status packet(active,typing etc)
             */
            StanzaFilter filter = new StanzaTypeFilter(Message.class);
            connection.addSyncStanzaListener(new ChatMessageListener(), filter);

            /**
             * Listener for acknowledging received receipts
             */
            DeliveryReceiptManager.getInstanceFor(connection).setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
            DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(new ReceiptReceivedListener() {
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


            /*
             * Listener for read status
             */
            ReadReceiptManager.getInstanceFor(connection).addReadReceiptReceivedListener(new ReadReceiptReceivedListener() {

                @Override
                public void onReceiptReceived(String fromJid, String toJid, String packetId) {
                    PersonalMessaging.getInstance(getApplicationContext()).readReceiptReceived(fromJid, toJid, packetId);
                }

            });

            connection.addPacketInterceptor(new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Log.i("Presence Intercepted", "true");
                    Presence presence = (Presence) packet;
                    if (CustomAppCompatActivity.isActivityCounterNull()) {
                        presence.setStatus("Offline");
                    } else {
                        presence.setStatus("Online");
                    }
                }
            }, new StanzaTypeFilter(Presence.class));

            /**
             *  packet filter to see if messages are published to the node or not
             */
            connection.addAsyncStanzaListener(new StanzaListener() {

                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    if (packet.getFrom().equals("pubsub.mm.io")) {
                        PubSubMessaging.getInstance().updatePublishedReceipt(getApplicationContext(), packet.getStanzaId());
                    } else {
                        //nothing
                    }
                }

            }, IQTypeFilter.RESULT);

            /**
             * Listen for subscription packets to read status
             */
            connection.addSyncStanzaListener(new StanzaListener() {

                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Log.i("Received presence", "subscribe");
                    Presence presence = (Presence) packet;
                    String from = presence.getFrom();
                    if (presence.getType() == Presence.Type.subscribe) {
                        try {
                            Log.i("sent presence", "subscribing");
                            Presence presence1 = new Presence(Presence.Type.subscribed);
                            presence1.setTo(from);
                            connection.sendStanza(presence1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (presence.getType() == Presence.Type.unsubscribe) {
                        try {
                            Log.i("sent presence", "subscribing");
                            Presence presence1 = new Presence(Presence.Type.unsubscribed);
                            presence1.setTo(from);
                            connection.sendStanza(presence1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String jid = from.substring(0, from.indexOf("@mm.io"));
                        if ("Online".equals(presence.getStatus())) {
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, jid, Presence.Type.available);
                        } else {
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, jid, Presence.Type.unavailable);
                        }
                    }
                }
            }, new StanzaTypeFilter(Presence.class));

        } else {
            //nothing
        }
    }

    private class XmppConnectionListener implements ConnectionListener {


        @Override
        public void connected(XMPPConnection connection) {
            Log.i("XMPP Connection", "connected");
            if (UserUtil.isProfileCreated()) {
                RosterHandler.getInstance(XMPPService.this.getApplicationContext()).loadRoster(connection);
                XMPPClient.getInstance().authenticateConnection(XMPPService.this);
            } else {
                //nothing
            }
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            try {
                Log.i("XMPP Connection", "authenticated");

                PubSubUtil.initialSetup();
                attachChatRelatedListeners((XMPPTCPConnection) connection);

                PersonalMessaging.getInstance(XMPPService.this).updateBlockList(XMPPService.this);
                GlobalEventHandler.getInstance().xmppServerConnected(true, connection);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void connectionClosed() {
            Log.i("connection", "closed");

            GlobalEventHandler.getInstance().xmppServerConnected(false, null);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.i("connection", "closed on error");

            GlobalEventHandler.getInstance().xmppServerConnected(false, null);
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i("reconnection", "succesful");

        }

        @Override
        public void reconnectingIn(int seconds) {

            GlobalEventHandler.getInstance().onReconnecting(seconds);
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.i("reconnection", "failed");

        }
    }

    private class ChatMessageListener implements StanzaListener {

        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            Message message = (Message) packet;
            if (message.getFrom().equals("pubsub.mm.io")) {
                PubSubMessaging.getInstance().handlePubSubMessage(XMPPService.this.getApplicationContext(), message);
            } else if (message.getType().equals(Message.Type.chat)) {
                if (message.getBody() == null) {
                    PersonalMessaging.getInstance(getApplicationContext()).handleStatus(message);
                } else {
                    PersonalMessaging.getInstance(getApplicationContext()).handleChatMessage(getApplicationContext(), message);
                }

            } else if (message.getType().equals(Message.Type.headline)) {
                if (message.getFrom().equals("pubsub.mm.io")) {
                    //TODO
                } else {
                    String body = message.getBody();
                    String jid = body.substring(0, body.indexOf('|'));
                    String gmtEpoch = body.substring(body.indexOf('|') + 1);
                    int days = CommonUtil.getTimeDifference(Long.parseLong(gmtEpoch));
                    if (days > 0) {
                        if (days == 1) {
                            String lastSeen = CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(gmtEpoch));
                            lastSeen = "yesterday at " + lastSeen;
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, jid, lastSeen);
                        } else if (days > 1 && days <= 3) {
                            String lastSeen = days + " days ago";
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, jid, lastSeen);
                        } else {
                            String lastSeen = CommonUtil.getDefaultTimezoneTime(Long.parseLong(gmtEpoch));
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, jid, lastSeen);
                        }
                    } else {
                        String lastSeen = CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(gmtEpoch));
                        lastSeen = "today at " + lastSeen;
                        ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, jid, lastSeen);
                    }

                }

            } else if (message.getType().equals(Message.Type.groupchat)) {
                if (message.getBody() == null) {
//                    PersonalMessaging.getInstance(getApplicationContext()).handleStatus(message);
                } else {
//                    PersonalMessaging.getInstance(getApplicationContext()).handleChatMessage(getApplicationContext(), message, true);
                }
            } else if (((Message) packet).getType().equals(Presence.Type.subscribe)) {
                //nothing
            }
        }

    }

}

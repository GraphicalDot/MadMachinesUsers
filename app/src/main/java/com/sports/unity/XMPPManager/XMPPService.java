package com.sports.unity.XMPPManager;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
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
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
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

    private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i("Service", "started");

        new Thread(new Runnable() {

            @Override
            public void run() {

                sportsUnityDBHelper = SportsUnityDBHelper.getInstance(XMPPService.this);
                SportsUnityDBHelper.getInstance(getApplicationContext()).addDummyMessageIfNotExist();

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
                public void invitationReceived(XMPPConnection conn, MultiUserChat multiUserChat, String inviter, String reason, String password, Message message) {
                    String subject = "";
                    /*Log.i("Group Chat", "new invitation from server");

                    String groupServerId = multiUserChat.getRoom().substring(0, multiUserChat.getRoom().indexOf("@conference.mm.io"));
                    String ownerPhoneNumber = inviter.substring(0, inviter.indexOf("@mm.io"));
                    String subject = multiUserChat.getSubject();

                    SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(getApplicationContext());

                    Contacts owner = sportsUnityDBHelper.getContact(ownerPhoneNumber);
                    if (owner == null) {
                        createContact(ownerPhoneNumber);
                        owner = sportsUnityDBHelper.getContact(ownerPhoneNumber);
                    }

                    long chatId = sportsUnityDBHelper.createGroupChatEntry(subject, owner.id, null, groupServerId);
                    sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), chatId, groupServerId);

                    String currentUserPhoneNumber = TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_USERNAME);
                    GroupMessaging.getInstance(getApplicationContext()).joinGroup(groupServerId, currentUserPhoneNumber);*/

                    String ownerJID = inviter.substring(0, inviter.indexOf("@mm.io"));

                    Contacts owner = sportsUnityDBHelper.getContactByJid(ownerJID);
                    if (owner == null) {
                        createContact(ownerJID, getApplicationContext(), true);
                        owner = sportsUnityDBHelper.getContactByJid(ownerJID);
                    }

                    String groupServerId = multiUserChat.getRoom().substring(0, multiUserChat.getRoom().indexOf("@"));

                    Log.i("group invitation ", "received");
                    PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
                    try {
                        LeafNode node = pubSubManager.getNode(groupServerId);
                        Log.i("subscribing to node ", "true");
                        node.subscribe(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_USER_JID) + "@mm.io");
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    subject = groupServerId.substring(groupServerId.indexOf("%") + 1, groupServerId.indexOf("%%"));
                    long chatId = sportsUnityDBHelper.createGroupChatEntry(subject, owner.id, null, groupServerId);
                    sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), chatId, groupServerId);
                    ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY, null);
                }

            });


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
                        Log.i("pubsub message", "published  :   " + packet.getStanzaId());
                        PubSubMessaging.getInstance(getApplicationContext()).updatePublishedReceipt(packet.getFrom(), packet.getStanzaId());
                        ;
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
                        if ("Online".equals(presence.getStatus())) {
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, Presence.Type.available);
                        } else {
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, Presence.Type.unavailable);
                        }
                    }
                }
            }, new StanzaTypeFilter(Presence.class));

        } else

        {
            //nothing
        }
    }

//    private void getForms(XMPPTCPConnection con) {
//        searchManager = new UserSearchManager(con);
//        try {
//            searchForm = searchManager.getSearchForm("vjud.mm.io");
//        } catch (SmackException.NoResponseException e) {
//            e.printStackTrace();
//        } catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//        }
//        answerForm = searchForm.createAnswerForm();
//        if (answerForm != null) {
//            new UpdateUserDetails().start();
//        }
//    }

    private class XmppConnectionListener implements ConnectionListener {


        @Override
        public void connected(XMPPConnection connection) {
            Log.i("XMPP Connection", "connected");
            if (UserUtil.isProfileCreated()) {
                XMPPClient.getInstance().authenticateConnection(XMPPService.this);
                /*Roster.getInstanceFor(connection).setSubscriptionMode(Roster.SubscriptionMode.accept_all);
                Roster.getInstanceFor(connection).addRosterListener(new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<String> addresses) {

                    }

                    @Override
                    public void entriesUpdated(Collection<String> addresses) {

                    }

                    @Override
                    public void entriesDeleted(Collection<String> addresses) {

                    }

                    @Override
                    public void presenceChanged(Presence presence) {
                        Log.d("max","User-->"+presence.getFrom()+"<<status>>"+presence.getStatus());
                    }
                });*/
            } else {
                //nothing
            }
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            try {
                Log.i("XMPP Connection", "authenticated");

                PersonalMessaging.getInstance(XMPPService.this).updateBlockList(XMPPService.this);

                attachChatRelatedListeners((XMPPTCPConnection) connection);
//                getForms((XMPPTCPConnection) connection);

                GlobalEventHandler.getInstance().xmppServerConnected(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void connectionClosed() {
            Log.i("connection", "closed");

            GlobalEventHandler.getInstance().internetStateChanged(false);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.i("connection", "closed on error");

            GlobalEventHandler.getInstance().internetStateChanged(false);
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i("reconnection", "succesful");

        }

        @Override
        public void reconnectingIn(int seconds) {
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
                handlePubSubMessage(message);
            }
            if (message.getType().equals(Message.Type.chat)) {

                if (message.getBody() == null) {
                    handleStatus(message);
                } else {
                    Log.i("Single Chat", "received");
                    handleChatMessage(message, false);
                }

            } else if (message.getType().equals(Message.Type.headline)) {
                if (message.getFrom().equals("pubsub.mm.io")) {
                    //TODO
                } else {
                    String gmtEpoch = message.getBody();
                    int days = CommonUtil.getTimeDifference(Long.parseLong(gmtEpoch));
                    if (days > 0) {
                        if (days == 1) {
                            String lastSeen = CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(gmtEpoch));
                            lastSeen = "yesterday at " + lastSeen;
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, lastSeen);
                        } else if (days > 1 && days <= 3) {
                            String lastSeen = days + " days ago";
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, lastSeen);
                        } else {
                            String lastSeen = CommonUtil.getDefaultTimezoneTime(Long.parseLong(gmtEpoch));
                            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, lastSeen);
                        }
                    } else {
                        String lastSeen = CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(gmtEpoch));
                        lastSeen = "today at " + lastSeen;
                        ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, lastSeen);
                    }

                }

            } else if (message.getType().equals(Message.Type.groupchat)) {
                if (message.getBody() == null) {
                    handleStatus(message);
                } else {
                    Log.i("Group Chat", "received");
                    handleChatMessage(message, true);
                }
            } else if (((Message) packet).getType().equals(Presence.Type.subscribe)) {

            }
        }

    }

    private void handlePubSubMessage(Message message) {

        String messageXML = message.toString();
        String data = messageXML.substring(messageXML.indexOf("!@#$") + 4, messageXML.indexOf("$#@!"));
        String from = "";
        String decodedData = "";
        JSONObject payLoad = null;
        try {
            decodedData = URLDecoder.decode(data, "utf-8");
            payLoad = new JSONObject(decodedData);
            from = payLoad.getString(PubSubMessaging.MESSAGE_FROM);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (from.equals(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_USER_JID))) {
            //Do nothing
        } else {
            String groupServerId = null;
            long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
            String time = null;
            String text = null;
            String nodeid = null;
            try {
                time = payLoad.getString(PubSubMessaging.MESSAGE_TIME);
                text = payLoad.getString(PubSubMessaging.MESSAGE_TEXT_DATA);
                nodeid = payLoad.getString(PubSubMessaging.GROUP_SERVER_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            groupServerId = nodeid;
            chatId = getChatIdOrCreateIfNotExist(true, from, groupServerId, false);
            handlePubSubMessageType(payLoad, chatId, from, text, time, message.getStanzaId(), groupServerId);
            if (ChatScreenApplication.isActivityVisible()) {
                if (nodeid.equals(ChatScreenActivity.getGroupServerId())) {
                    ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_SCREEN_KEY, null);
                } else {
                    sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                    ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY, null);
                }
            } else {
                sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY, null);
            }
        }

    }

    private void handlePubSubMessageType(JSONObject payLoad, long chatId, String from, String text, String time, String stanzaId, String nodeid) {
        String mimeType = null;
        try {
            mimeType = payLoad.getString(Constants.PARAM_MIME_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(text);
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(text);

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

            long messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, nodeid);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            long messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, nodeid);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            String checksum = text;

            long messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, nodeid);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(text);
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(text);

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

            long messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, nodeid);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            long messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null,
                    chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, nodeid);
        }
    }

    private void handleChatMessage(Message message, boolean isGroupChat) {

        Object value = JivePropertiesManager.getProperty(message, Constants.PARAM_TIME);
        String fromId = message.getFrom().substring(0, message.getFrom().indexOf("@"));
        String to = message.getTo().substring(0, message.getTo().indexOf("@"));
        String mimeType = (String) JivePropertiesManager.getProperty(message, Constants.PARAM_MIME_TYPE);
        boolean nearByChat = false;
        Object object = JivePropertiesManager.getProperty(message, Constants.PARAM_CHAT_TYPE_OTHERS);
        if (object != null) {
            nearByChat = (boolean) object;
        }

        boolean success = true;
        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

        String groupServerId = null;
        String messageFrom = null;
        if (isGroupChat) {
            groupServerId = fromId;
            messageFrom = message.getFrom().substring(message.getFrom().indexOf("/") + 1);

            if (!to.equals(messageFrom)) {
                chatId = getChatIdOrCreateIfNotExist(isGroupChat, messageFrom, groupServerId, false);
                handleMessage(message, value, chatId, messageFrom, groupServerId, false);
            } else {
                success = false;
            }
        } else {
            groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
            messageFrom = fromId;

            chatId = getChatIdOrCreateIfNotExist(isGroupChat, fromId, groupServerId, nearByChat);
            handleMessage(message, value, chatId, fromId, groupServerId, nearByChat);
        }


        if (success == true && chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {

            if (ChatScreenApplication.isActivityVisible()) {
                if (ChatScreenActivity.getJABBERID().equals(fromId)) {
                    ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_SCREEN_KEY, null);
                } else {
                    try {
                        sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                        if (nearByChat) {
                            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_OTHERS_LIST_KEY, null);
                        } else {
                            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY, null);
                        }
                        byte[] image = sportsUnityDBHelper.getUserProfileImage(fromId);
                        DisplayNotification(message.getBody(), messageFrom, mimeType, chatId, isGroupChat, groupServerId, image);
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
                    sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                    if (nearByChat) {
                        ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_OTHERS_LIST_KEY, null);
                    } else {
                        ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY, null);
                    }
                    byte[] image = sportsUnityDBHelper.getUserProfileImage(fromId);
                    DisplayNotification(message.getBody(), messageFrom, mimeType, chatId, isGroupChat, groupServerId, image);
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void handleStatus(Message message) {
        Log.i("handle status :", "");
        if (message.hasExtension(ChatState.composing.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "composing");
            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, ChatState.composing.toString());
        } else if (message.hasExtension(ChatState.active.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "active");
        } else if (message.hasExtension(ChatState.gone.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "gone");
        } else if (message.hasExtension(ChatState.paused.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "paused");
            ActivityActionHandler.getInstance().dispatchUserStatusOnChat(ActivityActionHandler.CHAT_SCREEN_KEY, ChatState.paused.toString());
        } else if (message.hasExtension(ChatState.inactive.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "inactive");
        }
    }

    private long getChatIdOrCreateIfNotExist(boolean isGroupChat, String from, String fromGroup, boolean nearByChat) {
        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

        if (!isGroupChat) {
            Contacts contact = sportsUnityDBHelper.getContactByJid(from);
            if (contact == null) {
                createContact(from, getApplicationContext(), nearByChat);
                contact = sportsUnityDBHelper.getContactByJid(from);
            }

            chatId = sportsUnityDBHelper.getChatEntryID(contact.id, fromGroup);
            if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
                //nothing
            } else {
                chatId = sportsUnityDBHelper.createChatEntry(contact.name, contact.id, nearByChat);
                Log.i("ChatEntry : ", "chat entry made from server " + chatId + " , " + contact.id);
            }

        } else {
            chatId = sportsUnityDBHelper.getChatEntryID(fromGroup);
        }
        return chatId;
    }

    public void handleMessage(Message message, Object value, long chatId, String from, String fromGroup, boolean nearByChat) {
        String mimeType = (String) JivePropertiesManager.getProperty(message, Constants.PARAM_MIME_TYPE);

        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(message.getBody());
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(message.getBody());

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

            long messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            String checksum = message.getBody();

            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(message.getBody());
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(message.getBody());

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

//            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
//                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            long messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null,
                    chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);
        }
    }

    public static boolean createContact(String jid, Context context, boolean nearByChat) {
        boolean success = false;
        try {
            XMPPTCPConnection connection = XMPPClient.getInstance().getConnection();
            VCard card = new VCard();
            card.load(connection, jid + "@mm.io");
            String status = card.getMiddleName();
            byte[] image = card.getAvatar();
            String nickname = card.getNickName();

            if (nearByChat) {
                SportsUnityDBHelper.getInstance(context).addToContacts(nickname, null, jid, ContactsHandler.getInstance().defaultStatus, null, SportsUnityDBHelper.AVAILABLE_BY_PEOPLE_AROUND_ME);
            } else {
                SportsUnityDBHelper.getInstance(context).addToContacts(nickname, null, jid, ContactsHandler.getInstance().defaultStatus, null, SportsUnityDBHelper.AVAILABLE_BY_OTHER_CONTACTS);
            }
            SportsUnityDBHelper.getInstance(context).updateContacts(jid, image, status);

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

//    public void DisplayNotification(String message, String from, long chatId, boolean isGroupChat, String groupServerId) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
//
////        String number = message.getFrom().substring(0, message.getFrom().indexOf("@"));
//
//        final  String GROUP_KEY_CHATS = "group_key_chats";
//        if (sportsUnityDBHelper.isMute(chatId)) {
//            //nothing
//        } else {
//            String name = sportsUnityDBHelper.getUserNameByJid(from);
//
//            Contacts contact = sportsUnityDBHelper.getContact(from);
//
//            Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
//            notificationIntent.putExtra("name", name);
//            notificationIntent.putExtra("number", from);
//            notificationIntent.putExtra("chatId", chatId);
//            notificationIntent.putExtra("contactId", contact.id);
//            notificationIntent.putExtra("groupServerId", groupServerId);
//            notificationIntent.putExtra("userpicture", contact.image);
//
//            Intent backIntent = new Intent(this, MainActivity.class);
//            backIntent.putExtra("tab_index", 2);
//            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//
//            UserUtil.init(this);
//
//            PendingIntent pendingIntent = PendingIntent.getActivities(this, NotificationHandler.NOTIFICATION_ID, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
//            builder.setSmallIcon(R.drawable.ic_stat_notification);
//            if (isGroupChat) {
//                builder.setContentText(name + " sent a Message");
//                builder.setContentTitle(sportsUnityDBHelper.getGroupSubject(groupServerId));
//            } else {
//                builder.setContentText(message);
//                builder.setContentTitle(name);
//            }
//
//
//            builder.setContentIntent(pendingIntent);
//            builder.setPriority(Notification.PRIORITY_HIGH);
//            builder.setDefaults(Notification.DEFAULT_ALL);
//            builder.setAutoCancel(true);
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.notify(NotificationHandler.NOTIFICATION_ID, builder.build());
//
//        }
//
//    }

    public void DisplayNotification(String message, String from, String mimeType, long chatId, boolean isGroupChat, String groupServerId, byte[] image) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        if (sportsUnityDBHelper.isMute(chatId)) {
            //nothing
        } else {
            UserUtil.init(this);

            String name = sportsUnityDBHelper.getUserNameByJid(from);
            if (isGroupChat) {
                name = name + "@" + sportsUnityDBHelper.getGroupSubject(groupServerId);
            } else {
                //nothing
            }

            NotificationHandler notificationHandler = NotificationHandler.getInstance(getApplicationContext());
            notificationHandler.addNotificationMessage(chatId, name, message, mimeType, image);

            int chatCount = notificationHandler.getNotificationChatCount();
            PendingIntent pendingIntent = null;
            if (chatCount > 1) {
                pendingIntent = getPendingIntentForMainActivity();
            } else if (chatCount == 1) {
                Contacts contact = sportsUnityDBHelper.getContactByJid(from);
                pendingIntent = getPendingIntentForChatActivity(name, from, chatId, contact.id, groupServerId, contact.image);
            }

            notificationHandler.showNotification(getApplicationContext(), pendingIntent);
            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.UNREAD_COUNT_KEY);
        }
    }

    private PendingIntent getPendingIntentForMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("tab_index", 2);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, NotificationHandler.NOTIFICATION_ID, new Intent[]{mainIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private PendingIntent getPendingIntentForChatActivity(String name, String from, long chatId, long contactId, String groupServerId, byte[] contactImage) {
        Intent notificationIntent;
//        notificationIntent.putExtra("name", name);
//        notificationIntent.putExtra("number", from);
//        notificationIntent.putExtra("chatId", chatId);
//        notificationIntent.putExtra("contactId", contactId);
//        notificationIntent.putExtra("groupServerId", groupServerId);
//        notificationIntent.putExtra("userpicture", contactImage);

        notificationIntent = ChatScreenActivity.createChatScreenIntent(getApplicationContext(), from, name, contactId, chatId, groupServerId, contactImage, false);

        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.putExtra("tab_index", 2);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, NotificationHandler.NOTIFICATION_ID, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

//    private class UpdateUserDetails extends Thread {
//
//        @Override
//        public void run() {
//            try {
//                Log.i("VCard Update", "Started");
//                ContactsHandler.getInstance().updateRegisteredUsers(XMPPService.this);
//                Log.i("VCard Update", "Ended");
//            } catch (XMPPException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

}

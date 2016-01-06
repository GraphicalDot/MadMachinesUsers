package com.sports.unity.XMPPManager;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.ContactsObserver;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.fragment.MessagesFragment;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.FileOnCloudHandler;
import com.sports.unity.util.NotificationHandler;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XMPPService extends Service {

    public static UserSearchManager searchManager;
    public static Form searchForm = null;
    public static Form answerForm = null;

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

    class ItemEventCoordinator implements ItemEventListener {
        @Override
        public void handlePublishedItems(ItemPublishEvent items) {
            Log.i("nodeId", items.getNodeId());

            List<Item> l = items.getItems();
            for (Item i :
                    l) {
                Log.i("payload ", i.toXML());
            }

        }
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

                    String ownerPhoneNumber = inviter.substring(0, inviter.indexOf("@mm.io"));

                    Contacts owner = sportsUnityDBHelper.getContact(ownerPhoneNumber);
                    if (owner == null) {
                        createContact(ownerPhoneNumber, getApplicationContext(), true);
                        owner = sportsUnityDBHelper.getContact(ownerPhoneNumber);
                    }

                    String groupServerId = multiUserChat.getRoom().substring(0, multiUserChat.getRoom().indexOf("@"));
                    Log.i("groupserverId", groupServerId);

                    Log.i("invitation recv", "true");
                    PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
                    try {
                        LeafNode node = pubSubManager.getNode(groupServerId);
                        Log.i("Subscribing", "true");
                        node.subscribe(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_USERNAME) + "@mm.io");
                        Log.i("fetchingaffiliations", "true");
                        node.getAffiliations();
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
                    sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
                }

            });


            /**
             * Listener for acknowledging recieved receipts
             */
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

            /**
             *  packet filter to see if messages are published to the node or not
             */

            /*connection.addPacketListener(new PacketListener() {

                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                }
            }, new IQTypeFilter(IQ.Type.result));*/


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
                    } else /*if (presence.getType() == Presence.Type.available)*/ {
                        if ("Online".equals(presence.getStatus())) {
                            sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, Presence.Type.available);
                        } else {
                            sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, Presence.Type.unavailable);
                        }
                    }
                }

            }, new PacketTypeFilter(Presence.class));

        } else

        {
            //nothing
        }
    }


    private void getForms(XMPPTCPConnection con) {
        searchManager = new UserSearchManager(con);
        try {
            searchForm = searchManager.getSearchForm("vjud.mm.io");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        answerForm = searchForm.createAnswerForm();
        if (answerForm != null) {
            new UpdateUserDetails().start();
        }

    }

    private class XmppConnectionListener implements ConnectionListener {


        @Override
        public void connected(XMPPConnection connection) {
            Log.i("XMPP Connection", "connected");

            if (UserUtil.isProfileCreated()) {
                XMPPClient.getInstance().authenticateConnection(XMPPService.this);
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
                getForms((XMPPTCPConnection) connection);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void connectionClosed() {
//            XMPPClient.reconnectAndAuthenticate_OnThread(XMPPService.this);
            Log.i("connection", "closed");

        }

        @Override
        public void connectionClosedOnError(Exception e) {
//            XMPPClient.reconnectAndAuthenticate_OnThread(XMPPService.this);
            Log.i("connection", "closed on error");
        }

        @Override
        public void reconnectionSuccessful() {
//            XMPPClient.reconnectAndAuthenticate_OnThread(XMPPService.this);
            Log.i("reconnection", "succesful");

        }

        @Override
        public void reconnectingIn(int seconds) {
        }

        @Override
        public void reconnectionFailed(Exception e) {
//            XMPPClient.reconnectAndAuthenticate_OnThread(XMPPService.this);
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
                    int days = Integer.parseInt(CommonUtil.getTimeDifference(Long.parseLong(gmtEpoch)));
                    if (days > 0) {
                        if (days == 1) {
                            String lastSeen = CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(gmtEpoch));
                            lastSeen = "Yesterday at " + lastSeen;
                            sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, lastSeen);
                        } else {
                            String lastSeen = days + " ago";
                            sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, lastSeen);
                        }
                    } else {
                        String lastSeen = CommonUtil.getDefaultTimezoneTimeInAMANDPM(Long.parseLong(gmtEpoch));
                        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, lastSeen);
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

        Log.i("pubsubmessagerecv", "true");
        String messageXML = message.toString();
        String from = messageXML.substring(messageXML.indexOf("!") + 1, messageXML.indexOf("!!"));
        if (from.equals(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_USERNAME))) {
            //Do nothing

        } else {
            String groupServerId = null;
            long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
            String time = messageXML.substring(messageXML.indexOf("*") + 1, messageXML.indexOf("**"));
            String text = messageXML.substring(messageXML.indexOf("$") + 1, messageXML.indexOf("$$"));
            String nodeid = messageXML.substring(messageXML.indexOf("node='") + 6, messageXML.indexOf("'><item id='"));
            groupServerId = nodeid;
            chatId = getChatIdOrCreateIfNotExist(true, from, groupServerId, false);
            long messageId = sportsUnityDBHelper.addMessage(text, SportsUnityDBHelper.MIME_TYPE_TEXT, from, false, time, null, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupServerId);
            if (ChatScreenApplication.isActivityVisible()) {
                if (nodeid.equals(ChatScreenActivity.getGroupServerId())) {
                    sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, null);
                } else {
                    sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                    sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
                    try {
                        DisplayNotification(text, from, SportsUnityDBHelper.MIME_TYPE_TEXT, chatId, true, groupServerId);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
                try {
                    DisplayNotification(text, from, SportsUnityDBHelper.MIME_TYPE_TEXT, chatId, true, groupServerId);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                }
            }
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
                    sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, null);
                } else {
                    try {
                        sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
                        DisplayNotification(message.getBody(), messageFrom, mimeType, chatId, isGroupChat, groupServerId);
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
                    sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
                    DisplayNotification(message.getBody(), messageFrom, mimeType, chatId, isGroupChat, groupServerId);
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
            sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, ChatState.composing.toString());
        } else if (message.hasExtension(ChatState.active.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "active");
        } else if (message.hasExtension(ChatState.gone.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "gone");
        } else if (message.hasExtension(ChatState.paused.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "paused");
            sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, ChatState.paused.toString());
        } else if (message.hasExtension(ChatState.inactive.toString(), ChatStateExtension.NAMESPACE)) {
            Log.i("status :", "inactive");
        }
    }

    private long getChatIdOrCreateIfNotExist(boolean isGroupChat, String from, String fromGroup, boolean nearByChat) {
        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

        if (!isGroupChat) {
            Contacts contact = sportsUnityDBHelper.getContact(from);
            if (contact == null) {
                createContact(from, getApplicationContext(), nearByChat);
                contact = sportsUnityDBHelper.getContact(from);
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
            String checksum = message.getBody();

            long messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);

            if (!nearByChat) {
                sendActionToCorrespondingActivityListener(3, ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
            } else {
                //TODO
            }
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            String checksum = message.getBody();

            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);

            if (!nearByChat) {
                sendActionToCorrespondingActivityListener(3, ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
            } else {
                //TODO
            }
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String checksum = message.getBody();

            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);

            if (!nearByChat) {
                sendActionToCorrespondingActivityListener(3, ActivityActionHandler.CHAT_SCREEN_KEY, mimeType, checksum, Long.valueOf(messageId));
            } else {
                //TODO
            }
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            long messageId = sportsUnityDBHelper.addMessage(message.getBody().toString(), mimeType, from, false,
                    value.toString(), message.getStanzaId(), null, null,
                    chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);
        }
    }

    private boolean sendActionToCorrespondingActivityListener(String key, int id, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            if (data == null) {
                actionListener.handleAction(id);
            } else {
                actionListener.handleAction(id, data);
            }
            success = true;
        }
        return success;
    }

    private boolean sendActionToCorrespondingActivityListener(int id, String key, String mimeType, Object messageContent, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(id, mimeType, messageContent, mediaContent);
            success = true;
        }
        return success;
    }

    public static boolean createContact(String number, Context context, boolean nearByChat) {
        boolean success = false;
        try {
            XMPPTCPConnection connection = XMPPClient.getInstance().getConnection();
            VCard card = new VCard();
            card.load(connection, number + "@mm.io");
            String status = card.getMiddleName();
            byte[] image = card.getAvatar();
            String nickname = card.getNickName();

            if (nearByChat) {
                SportsUnityDBHelper.getInstance(context).addToContacts(nickname, number, true, ContactsHandler.getInstance().defaultStatus, false);
            } else {
                SportsUnityDBHelper.getInstance(context).addToContacts(number, number, true, ContactsHandler.getInstance().defaultStatus, true);
            }
            SportsUnityDBHelper.getInstance(context).updateContacts(number, image, status);

            success = true;
        } catch (Throwable throwable) {

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
//            String name = sportsUnityDBHelper.getJabberName(from);
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

    public void DisplayNotification(String message, String from, String mimeType, long chatId, boolean isGroupChat, String groupServerId) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        if (sportsUnityDBHelper.isMute(chatId)) {
            //nothing
        } else {
            UserUtil.init(this);

            String name = sportsUnityDBHelper.getJabberName(from);
            if (isGroupChat) {
                name = name + "@" + sportsUnityDBHelper.getGroupSubject(groupServerId);
            } else {
                //nothing
            }

            NotificationHandler notificationHandler = NotificationHandler.getInstance();
            notificationHandler.addNotificationMessage(chatId, name, message, mimeType);

            int chatCount = notificationHandler.getNotificationChatCount();
            PendingIntent pendingIntent = null;
            if (chatCount > 1) {
                pendingIntent = getPendingIntentForMainActivity();
            } else if (chatCount == 1) {
                Contacts contact = sportsUnityDBHelper.getContact(from);
                pendingIntent = getPendingIntentForChatActivity(name, from, chatId, contact.id, groupServerId, contact.image);
            }

            notificationHandler.showNotification(getApplicationContext(), pendingIntent, chatId);
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
        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("number", from);
        notificationIntent.putExtra("chatId", chatId);
        notificationIntent.putExtra("contactId", contactId);
        notificationIntent.putExtra("groupServerId", groupServerId);
        notificationIntent.putExtra("userpicture", contactImage);

        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.putExtra("tab_index", 2);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, NotificationHandler.NOTIFICATION_ID, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private class UpdateUserDetails extends Thread {

        @Override
        public void run() {
            try {
                Log.i("VCard Update", "Started");
                ContactsHandler.getInstance().updateRegisteredUsers(XMPPService.this);
                Log.i("VCard Update", "Ended");
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }

    }

}

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
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.ContactsObserver;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.GroupMessaging;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;

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
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.text.SimpleDateFormat;

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

    private int mNotificationId = 1;

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
                    Log.i("Group Chat", "new invitation from server");

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
                    GroupMessaging.getInstance(getApplicationContext()).joinGroup(groupServerId, currentUserPhoneNumber);
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
                    } else if (presence.getType() == Presence.Type.available) {
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
            if (message.getType().equals(Message.Type.chat)) {
                if (message.getBody() == null) {
                    handleStatus(message);
                } else {
                    Log.i("Single Chat", "received");
                    handleChatMessage(message, false);
                }

            } else if (message.getType().equals(Message.Type.headline)) {
                String lastSeen = message.getBody();
                DateTime dateTimenow = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());
                DateTime dateTime = new DateTime(Long.valueOf(lastSeen));
                lastSeen = getTimeBetween(dateTime, dateTimenow);

                sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, lastSeen);
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

    private String getTimeBetween(DateTime dateTime, DateTime dateTimenow) {
        int days = Days.daysBetween(dateTime, dateTimenow).getDays();
        int hours = Hours.hoursBetween(dateTime, dateTimenow).getHours();
        int minutes = Minutes.minutesBetween(dateTime, dateTimenow).getMinutes();
        int seconds = Seconds.secondsBetween(dateTime, dateTimenow).getSeconds();
        if (days > 0) {
            return String.valueOf(days + " days");
        } else if (hours > 0) {
            return String.valueOf(hours + " hours");
        } else if (minutes > 0) {
            return String.valueOf(minutes + " minutes");
        } else {
            return String.valueOf(seconds + " seconds");
        }
    }

    private void handleChatMessage(Message message, boolean isGroupChat) {

        Object value = JivePropertiesManager.getProperty(message, "time");
        String fromId = message.getFrom().substring(0, message.getFrom().indexOf("@"));
        String to = message.getTo().substring(0, message.getTo().indexOf("@"));

        boolean success = true;
        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

        String groupServerId = null;
        String messageFrom = null;
        if (isGroupChat) {
            groupServerId = fromId;
            messageFrom = message.getFrom().substring(message.getFrom().indexOf("/") + 1);

            if (!to.equals(messageFrom)) {
                chatId = getChatIdOrCreateIfNotExist(isGroupChat, messageFrom, groupServerId);
                addToDatabase(message, value, chatId, messageFrom, groupServerId);
            } else {
                success = false;
            }
        } else {
            groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
            messageFrom = fromId;

            chatId = getChatIdOrCreateIfNotExist(isGroupChat, fromId, groupServerId);
            addToDatabase(message, value, chatId, fromId, groupServerId);
        }


        if (success == true && chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {

            if (ChatScreenApplication.isActivityVisible()) {
                if (ChatScreenActivity.getJABBERID().equals(fromId)) {
                    sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, null);
                } else {
                    try {
                        sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
                        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
                        DisplayNotification(message.getBody(), messageFrom, chatId, isGroupChat, groupServerId);
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
                    DisplayNotification(message.getBody(), messageFrom, chatId, isGroupChat, groupServerId);
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

            Object value = JivePropertiesManager.getProperty(message, "time");
            DateTime dateTime = new DateTime(value);
            SimpleDateFormat formatter = new SimpleDateFormat("k:mm");
            //sportsUnityDBHelper.getInstance(getApplicationContext()).updateStatus(String.valueOf(formatter.format(dateTime.getMillis()), getChatId()));
        }
    }

    private long getChatIdOrCreateIfNotExist(boolean isGroupChat, String from, String fromGroup) {
        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

        if (!isGroupChat) {
            Contacts contact = sportsUnityDBHelper.getContact(from);
            if (contact == null) {
                createContact(from);
                contact = sportsUnityDBHelper.getContact(from);
            }

            chatId = sportsUnityDBHelper.getChatEntryID(contact.id, fromGroup);
            if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
                //nothing
            } else {
                chatId = sportsUnityDBHelper.createChatEntry(contact.name, contact.id);
                Log.i("ChatEntry : ", "chat entry made from server " + chatId + " , " + contact.id);
            }

        } else {
            chatId = sportsUnityDBHelper.getChatEntryID(fromGroup);
        }
        return chatId;
    }

    public void addToDatabase(Message message, Object value, long chatId, String from, String fromGroup) {
        DateTime dateTime = new DateTime(value);

        long messageId = sportsUnityDBHelper.addTextMessage(message.getBody().toString(), from, false,
                String.valueOf(dateTime.getMillis()), message.getStanzaId(), null, null,
                chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
        sportsUnityDBHelper.updateChatEntry(messageId, chatId, fromGroup);
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

    private boolean createContact(String number) {
        boolean success = false;
        try {
            XMPPTCPConnection connection = XMPPClient.getInstance().getConnection();
            VCard card = new VCard();
            card.load(connection, number + "@mm.io");
            String status = card.getMiddleName();
            byte[] image = card.getAvatar();

            sportsUnityDBHelper.addToContacts(number, number, true, ContactsHandler.getInstance().defaultStatus, true);
            sportsUnityDBHelper.updateContacts(number, image, status);

            success = true;
        } catch (Throwable throwable) {

        }
        return success;
    }

    public void DisplayNotification(String message, String from, long chatId, boolean isGroupChat, String groupServerId) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

//        String number = message.getFrom().substring(0, message.getFrom().indexOf("@"));

        if (sportsUnityDBHelper.isMute(chatId)) {
            //nothing
        } else {
            String name = sportsUnityDBHelper.getJabberName(from);

            Contacts contact = sportsUnityDBHelper.getContact(from);

            Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
            notificationIntent.putExtra("name", name);
            notificationIntent.putExtra("number", from);
            notificationIntent.putExtra("chatId", chatId);
            notificationIntent.putExtra("contactId", contact.id);
            notificationIntent.putExtra("groupServerId", groupServerId);
            notificationIntent.putExtra("userpicture", contact.image);

            Intent backIntent = new Intent(this, MainActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            UserUtil.init(this);

            PendingIntent pendingIntent = PendingIntent.getActivities(this, mNotificationId, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.drawable.ic_stat_notification);
            builder.setContentText(message);
            if (name != null && !name.isEmpty()) {
                builder.setContentTitle(name);
            } else {
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
        }

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

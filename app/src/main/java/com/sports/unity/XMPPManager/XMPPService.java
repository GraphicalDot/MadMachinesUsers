package com.sports.unity.XMPPManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.ContactsObserver;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
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
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
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
            if (!mConnection.isAuthenticated()) {
                mConnection.login(tinyDB.getString(TinyDB.KEY_USERNAME), tinyDB.getString(TinyDB.KEY_PASSWORD));
                Presence presence = new Presence(Presence.Type.unavailable);
                mConnection.sendPacket(presence);
            }

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
            public void invitationReceived(XMPPConnection conn, MultiUserChat multiUserChat, String inviter, String reason, String password, Message message) {

                Log.i("Group Chat", "new invitation from server");

                String groupServerId = multiUserChat.getRoom().substring(0, multiUserChat.getRoom().indexOf("@conference.mm.io"));
                String ownerPhoneNumber = inviter.substring(0, inviter.indexOf("@mm.io"));
                Log.i("Group Server ID", "" + groupServerId);

                SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(getApplicationContext());

                SportsUnityDBHelper.Contacts owner = sportsUnityDBHelper.getContact(ownerPhoneNumber);
                long chatId = sportsUnityDBHelper.createGroupChatEntry(reason, owner.id, null, groupServerId);
                sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), chatId, groupServerId);

                String currentUserPhoneNumber = TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_USERNAME);
                GroupMessaging.getInstance(getApplicationContext()).joinGroup(groupServerId, currentUserPhoneNumber);

//                try {
//                    room.join(TinyDB.getInstance(getApplicationContext()).getString("facebookname"));
//                } catch (SmackException.NoResponseException e) {
//                    e.printStackTrace();
//                } catch (XMPPException.XMPPErrorException e) {
//                    e.printStackTrace();
//                } catch (SmackException.NotConnectedException e) {
//                    e.printStackTrace();
//                }
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
                } else if (presence.getType() == Presence.Type.available) {

                } else if (presence.getType() == Presence.Type.unavailable) {

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
                    handleChatMessage(message, false);
                }
            } else if (message.getType().equals(Message.Type.groupchat)) {
                Log.i("Group Chat", "new message fro server");

                if (message.getBody() == null) {
                    getStatus(message);
                } else {
                    handleChatMessage(message, true);
                }
            } else if (((Message) packet).getType().equals(Presence.Type.subscribe)) {
                Log.i("subscription", "recieved");
            }
        }
    }

    private void handleChatMessage(Message message, boolean isGroupChat) {

        Object value = JivePropertiesManager.getProperty(message, "time");
        String from = message.getFrom().substring(0, message.getFrom().indexOf("@"));
        String to = message.getTo().substring(0, message.getTo().indexOf("@"));

        boolean success = true;
        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
        String groupServerId = null;
        if (isGroupChat) {
            groupServerId = from;
            String messageFrom = message.getFrom().substring(message.getFrom().indexOf("/") + 1);

            if (!to.equals(messageFrom)) {
                chatId = getChatIdOrCreateIfNotExist(isGroupChat, messageFrom, groupServerId);
                addToDatabase(message, value, chatId, isGroupChat, messageFrom, groupServerId);
            } else {
                success = false;
            }
        } else {
            groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
            chatId = getChatIdOrCreateIfNotExist(isGroupChat, from, groupServerId);
            addToDatabase(message, value, chatId, isGroupChat, from, groupServerId);
        }

        if (success == true && chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {

            if (ChatScreenApplication.isActivityVisible()) {
                if (ChatScreenActivity.getJABBERID().equals(from)) {
//                addToDatabase(message, value, isGroupChat);
                    sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, null);
                } else {
                    try {
                        DisplayNotification(message, chatId, isGroupChat, groupServerId);
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
                    DisplayNotification(message, chatId, isGroupChat, groupServerId);
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

        }
        Log.i("personalmessage :", "recieved");
    }

    private void getStatus(Message message) {
        Log.i("getting status :", "true");
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
        getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, ContactsObserver.getInstance(new Handler(), this));
    }

    private long getChatIdOrCreateIfNotExist(boolean isGroupChat, String from, String fromGroup) {
        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

        if (!isGroupChat) {
            SportsUnityDBHelper.Contacts contact = sportsUnityDBHelper.getContact(from);
            if( contact == null ) {
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
//            if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
//                //nothing
//            } else {
//                chatId = sportsUnityDBHelper.createGroupChatEntry( "", contact.id, null, groupServerId);
//                Log.i("GroupChatEntry : ", "group chat entry made from server " + chatId + " , " + contact.id);
//            }
        }
        return chatId;
    }

    public void addToDatabase(Message message, Object value, long chatId, boolean isGroupChat, String from, String fromGroup) {
//        String from = message.getFrom().toString().substring(0, message.getFrom().indexOf("@"));
//        String to = message.getTo().toString().substring(0, message.getTo().indexOf("@"));

        DateTime dateTime = new DateTime(value);
        SimpleDateFormat formatter = new SimpleDateFormat("k:mm");
        Log.i("senttime : ", String.valueOf(formatter.format(dateTime.getMillis())));
        Log.i("Message Entry", "adding message from server chat " + chatId);

        long messageId = sportsUnityDBHelper.addTextMessage(message.getBody().toString(), from, false,
                String.valueOf(dateTime.getMillis()), message.getStanzaId(), null, null,
                chatId);
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

    public void DisplayNotification(Message message, long chatId, boolean isGroupChat, String groupServerId) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

        String number = message.getFrom().substring(0, message.getFrom().indexOf("@"));
        String name = sportsUnityDBHelper.getJabberName(number);

        SportsUnityDBHelper.Contacts contact = sportsUnityDBHelper.getContact(number);

        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("number", number);
        notificationIntent.putExtra("chatId", chatId);
        notificationIntent.putExtra("contactId", contact.id);
        notificationIntent.putExtra("groupServerId", groupServerId);
        notificationIntent.putExtra("userpicture", contact.image);

        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, mNotificationId, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setContentText(message.getBody().toString());
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

        if (isGroupChat) {
//            long chatId = sportsUnityDBHelper.getChatEntryID(groupServerId);
            sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
        } else {
            sportsUnityDBHelper.updateUnreadCount(chatId, groupServerId);
        }

        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
    }

    private boolean createContact(String number) {
        boolean success = false;
        try {
            VCard card = new VCard();
            card.load(mConnection, number + "@mm.io");
            String status = card.getMiddleName();
            byte[] image = card.getAvatar();

            sportsUnityDBHelper.addToContacts(number, number, true, ContactsHandler.getInstance().defaultStatus);
            sportsUnityDBHelper.updateContacts(number, image, status);

//        SportsUnityDBHelper.Contacts newAddedContact = sportsUnityDBHelper.getContact(number);

            success = true;
        } catch (Throwable throwable) {

        }
        return success;
    }

//    public void DisplayNotification(Message message, boolean isGroupChat, String groupServerId) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
//
//        String number = message.getFrom().substring(0, message.getFrom().indexOf("@"));
//        SportsUnityDBHelper.Contacts contact = sportsUnityDBHelper.getContact(number);
//        if (contact == null) {
//            newContact(message, number, isGroupChat, groupServerId);
//        } else {
//            existingContact(message, contact, number, isGroupChat, groupServerId);
//        }
//
//    }
//
//    private void newContact(Message message, String number, boolean isGroupChat, String groupServerId) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
//
//        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
//        VCard card = new VCard();
//        card.load(mConnection, number + "@mm.io");
//        String status = card.getMiddleName();
//        byte[] image = card.getAvatar();
//
//        chatId = createChatEntry(number, chatId, image, status);
//
//        SportsUnityDBHelper.Contacts newAddedContact = sportsUnityDBHelper.getContact(number);
//
//        Object value = JivePropertiesManager.getProperty(message, "time");
//        addToDatabase(message, value, isGroupChat);
//
//        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
//        notificationIntent.putExtra("number", number);
//        notificationIntent.putExtra("name", newAddedContact.name);
//        notificationIntent.putExtra("chatId", chatId);
//        notificationIntent.putExtra("contactId", newAddedContact.id);
//        notificationIntent.putExtra("userpicture", newAddedContact.image);
//
//        Intent backIntent = new Intent(this, MainActivity.class);
//        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivities(this, mNotificationId, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
//        builder.setSmallIcon(R.drawable.ic_stat_notification);
//        builder.setContentText(message.getBody().toString());
//        builder.setContentTitle(newAddedContact.name);
//        builder.setContentIntent(pendingIntent);
//        builder.setPriority(Notification.PRIORITY_HIGH);
//        builder.setDefaults(Notification.DEFAULT_ALL);
//        builder.setAutoCancel(true);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(mNotificationId, builder.build());
//        sportsUnityDBHelper.updateUnreadCount( chatId, groupServerId);
////        Intent intent = new Intent();
////        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
////        sendBroadcast(intent);
//        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
//
//
//    }
//
//    private void existingContact(Message message, SportsUnityDBHelper.Contacts contact, String number, boolean isGroupChat, String groupServerId) {
//
//        long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
//
//        String name = contact.name;
//        chatId = sportsUnityDBHelper.getChatEntryID(contact.id);
//
//        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
//        notificationIntent.putExtra("number", number);
//        notificationIntent.putExtra("name", name);
//        notificationIntent.putExtra("chatId", chatId);
//        notificationIntent.putExtra("contactId", contact.id);
//        notificationIntent.putExtra("userpicture", contact.image);
//
//        Object value = JivePropertiesManager.getProperty(message, "time");
//        addToDatabase(message, value, isGroupChat);
//
//        Intent backIntent = new Intent(this, MainActivity.class);
//        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivities(this, mNotificationId, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
//        builder.setSmallIcon(R.drawable.ic_stat_notification);
//
//        builder.setContentText(message.getBody().toString());
//        builder.setContentTitle(name);
//        builder.setContentIntent(pendingIntent);
//        builder.setPriority(Notification.PRIORITY_HIGH);
//        builder.setDefaults(Notification.DEFAULT_ALL);
//        builder.setAutoCancel(true);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(mNotificationId, builder.build());
//        sportsUnityDBHelper.updateUnreadCount( chatId, groupServerId);
////        Intent intent = new Intent();
////        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
////        sendBroadcast(intent);
//        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_LIST_KEY, 0, null);
//
//
//    }

//    private long createChatEntry(String number, long chatId, byte[] image, String status) {
//
//        /**
//         * first create a contact before adding a chat entry for it
//         */
//
//        sportsUnityDBHelper.addToContacts(number, number, true, ContactsHandler.getInstance().defaultStatus);
//        sportsUnityDBHelper.updateContacts(number, image, status);
//
//        chatId = sportsUnityDBHelper.createChatEntry(number, sportsUnityDBHelper.getContactId(number));
//
//
//        return chatId;
//    }

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

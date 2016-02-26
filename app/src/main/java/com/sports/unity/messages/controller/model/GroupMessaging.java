package com.sports.unity.messages.controller.model;

import android.content.Context;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by madmachines on 27/10/15.
 */
public class GroupMessaging {

    private static GroupMessaging GROUP_MESSAGING = null;

    synchronized public static GroupMessaging getInstance(Context context) {
        if (GROUP_MESSAGING == null) {
            GROUP_MESSAGING = new GroupMessaging(context);
        }
        return GROUP_MESSAGING;
    }

    private final Map<MultiUserChat, ChatState> chatStates = new WeakHashMap<MultiUserChat, ChatState>();

    private SportsUnityDBHelper sportsUnityDBHelper = null;

    private GroupMessaging(Context context) {
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
    }

    public boolean createGroup(String roomName, String ownerJID, String subject) {
        boolean success = false;

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(XMPPClient.getConnection());
        MultiUserChat multiUserChat = manager.getMultiUserChat(roomName + "@conference.mm.io");

        try {
            multiUserChat.create(ownerJID + "@mm.io");
            multiUserChat.changeSubject(subject);

            success = true;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean setGroupConfigDetail(String roomName, String groupName, String groupDescription) {
        boolean success = false;

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(XMPPClient.getConnection());
        MultiUserChat multiUserChat = manager.getMultiUserChat(roomName + "@conference.mm.io");

        try {
            Form form = multiUserChat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();

            for (FormField field : form.getFields()) {
                if (!FormField.Type.hidden.name().equals(field.getType()) && field.getVariable() != null) {
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }

//            submitForm.setAnswer("muc#roomconfig_publicroom", true);
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//            submitForm.setAnswer("muc#roomconfig_roomowners", owner.jid);

//            submitForm.setAnswer("muc#roomconfig_whois", Arrays.asList("none"));
            submitForm.setAnswer("muc#roomconfig_roomname", groupName);
            submitForm.setAnswer("muc#roomconfig_roomdesc", groupDescription);

            multiUserChat.sendConfigurationForm(submitForm);
            multiUserChat.changeSubject(groupName);

            success = false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean setGroupDetails(String roomName, String groupname, String groupDescription) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(XMPPClient.getConnection());
        MultiUserChat multiUserChat = manager.getMultiUserChat(roomName + "@conference.mm.io");

        return setGroupDetails(multiUserChat, groupname, groupDescription);
    }

    public boolean setGroupDetails(MultiUserChat multiUserChat, String groupName, String groupDescription) {
        boolean success = false;
        try {
            Form form = multiUserChat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();

            submitForm.setAnswer("muc#roomconfig_roomname", groupName);
            submitForm.setAnswer("muc#roomconfig_roomdesc", groupDescription);

            multiUserChat.sendConfigurationForm(submitForm);

            success = true;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean joinGroup(String roomName, String jid) {
        boolean success = false;

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(XMPPClient.getConnection());
        MultiUserChat multiUserChat = manager.getMultiUserChat(roomName + "@conference.mm.io");

        try {
            multiUserChat.join(jid);

            success = true;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        return success;
    }

    public void getRoomInfo(String roomName) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(XMPPClient.getConnection());
        RoomInfo roomInfo = manager.getRoomInfo(roomName + "@conference.mm.io");

        //TODO
//        return roomInfo;
    }

    public void inviteMembers(String roomName, ArrayList<Contacts> members, String message) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(XMPPClient.getConnection());
        MultiUserChat multiUserChat = manager.getMultiUserChat(roomName + "@conference.mm.io");

        for (int i = 0; i < members.size(); i++) {
            try {
                multiUserChat.invite(members.get(i).jid + "@mm.io", message);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMessageToGroup(String msg, MultiUserChat multiUserChat, long chatId, String groupServerId, String from) {
        Message message = new Message();
        message.setType(Message.Type.groupchat);
        message.setBody(msg);
        DateTime dateTime = DateTime.now();
        JivePropertiesManager.addProperty(message, Constants.PARAM_TIME, dateTime.getMillis());
        String id = message.getStanzaId();
        try {
            multiUserChat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        /**
         * query to insert this message into database
         * SportsUnityDBHelper.getInstance(context).addMessageToDatabase();
         */

        long messageId = sportsUnityDBHelper.addMessage(msg, SportsUnityDBHelper.MIME_TYPE_TEXT, from, true, null, id, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
        sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupServerId);

    }

    public void sendStatus(ChatState newState, MultiUserChat chat) {

        if (chat == null || newState == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

        if (!updateChatState(chat, newState)) {
            return;
        }

        Message message = new Message();
        DateTime dateTime = DateTime.now();
        JivePropertiesManager.addProperty(message, Constants.PARAM_TIME, dateTime.getMillis());
        ChatStateExtension extension = new ChatStateExtension(newState);
        message.addExtension(extension);
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void setReceivedReceipts(String fromJid, String receiptId, Context applicationContext) {
        /**
         * set read receipts in database
         */

        if (fromJid.substring(0, fromJid.indexOf("@")).equals("dev")) {
            sportsUnityDBHelper.updateServerReceived(receiptId);
            updateReceipts(PersonalMessaging.RECEIPT_KIND_SERVER);
        } else {
            sportsUnityDBHelper.updateClientReceived(receiptId);
            updateReceipts(PersonalMessaging.RECEIPT_KIND_CLIENT);
        }
    }

    public void setActiveStatus(String phoneNumber, String status) {

        /**
         * query to set active status in the database and then call getActiveStatus to display it
         */

    }

    public String getActiveStatus(String phoneNumber) {

        /**
         * query to get active status from the database and return it in status
         */
        String status = null;

        return status;

    }

    private synchronized boolean updateChatState(MultiUserChat chat, ChatState newState) {
        ChatState lastChatState = chatStates.get(chat);
        if (lastChatState != newState) {
            chatStates.put(chat, newState);
            return true;
        }
        return false;
    }

    private void updateReceipts(int receiptKind) {
        /**
         * get read receipts in database and then update the double ticks in the corresponding chats
         */
//        Intent intent = new Intent();
//        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
//        applicationContext.sendBroadcast(intent);

        ActivityActionHandler.getInstance().dispatchReceiptEvent(ActivityActionHandler.CHAT_SCREEN_KEY, receiptKind);
    }

}

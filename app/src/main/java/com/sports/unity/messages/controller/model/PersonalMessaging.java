package com.sports.unity.messages.controller.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by madmachines on 16/10/15.
 */
public class PersonalMessaging {

    private static PersonalMessaging pmessaging = null;

    synchronized public static PersonalMessaging getInstance(Context context) {
        if (pmessaging == null) {
            pmessaging = new PersonalMessaging(context);
        }
        return pmessaging;
    }

    private final Map<Chat, ChatState> chatStates = new WeakHashMap<Chat, ChatState>();

    private SportsUnityDBHelper sportsUnityDBHelper = null;

    private PersonalMessaging(Context context) {
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
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

    public void sendMessageToPeer(String msg, Chat chat, String number, long chatId, String name) {
        Message message = new Message();
        message.setBody(msg);
        DateTime dateTime = DateTime.now();
        JivePropertiesManager.addProperty(message, "time", dateTime.getMillis());
        JivePropertiesManager.addProperty(message, "isGroupChat", "F");
        DeliveryReceiptRequest.addTo(message);                                            //Request delivery receipts for this message
        String id = message.getStanzaId();
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        /**
         * query to insert this message into database
         * SportsUnityDBHelper.getInstance(context).addMessageToDatabase();
         */

        long messageId = sportsUnityDBHelper.addTextMessage(msg, number, true, null, id, null, null, chatId);
        sportsUnityDBHelper.updateChatEntry(messageId, chatId, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

    }

    public void sendStatus(ChatState newState, Chat chat) {

        if (chat == null || newState == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

        if (!updateChatState(chat, newState)) {
            return;
        }

        Message message = new Message();
        DateTime dateTime = DateTime.now();
        JivePropertiesManager.addProperty(message, "time", dateTime.getMillis());
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

        Log.i("deliveredto :", fromJid);
        Log.i("receiptiD :", receiptId);

        if (fromJid.substring(0, fromJid.indexOf("@")).equals("dev")) {
            sportsUnityDBHelper.updateServerReceived(receiptId);
        } else {
            sportsUnityDBHelper.updateClientReceived(receiptId);
        }

        updateReadreceipts(applicationContext);
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

    private synchronized boolean updateChatState(Chat chat, ChatState newState) {
        ChatState lastChatState = chatStates.get(chat);
        if (lastChatState != newState) {
            chatStates.put(chat, newState);
            return true;
        }
        return false;
    }
    
    public void updateReadreceipts(Context applicationContext) {

        /**
         * get read receipts in database and then update the double ticks in the corresponding chats
         */

        Log.i("Ticks :", "updated");
        /*Intent intent = new Intent();
        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
        applicationContext.sendBroadcast(intent);*/
        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, null);


    }

}

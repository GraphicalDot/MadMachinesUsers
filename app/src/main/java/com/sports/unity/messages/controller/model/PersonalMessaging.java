package com.sports.unity.messages.controller.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;

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

    private final Map<Chat, ChatState> chatStates = new WeakHashMap<Chat, ChatState>();

    private Context context;

    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

    private PersonalMessaging(Context context) {

        this.context = context;

    }

    synchronized public static PersonalMessaging getInstance(Context context) {
        if (pmessaging == null) {
            pmessaging = new PersonalMessaging(context);
        }
        return pmessaging;
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

        long messageId = sportsUnityDBHelper.addTextMessage(msg, number, true, null, id, null, null, chatId, SportsUnityDBHelper.DEFAULT_ENTRY_ID, false);
        sportsUnityDBHelper.updateChatEntry(messageId, chatId);


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

    private synchronized boolean updateChatState(Chat chat, ChatState newState) {
        ChatState lastChatState = chatStates.get(chat);
        if (lastChatState != newState) {
            chatStates.put(chat, newState);
            return true;
        }
        return false;
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
        /*if (ChatScreenApplication.isActivityVisible()) {
            if (ChatScreenActivity.getJABBERID().equals(fromJid)) {
                updateReadreceipts(applicationContext);
            }

        }*/
    }

    public void updateReadreceipts(Context applicationContext) {

        /**
         * get read receipts in database and then update the double ticks in the corresponding chats
         */

        Log.i("Ticks :", "updated");
        Intent intent = new Intent();
        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
        applicationContext.sendBroadcast(intent);

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

}

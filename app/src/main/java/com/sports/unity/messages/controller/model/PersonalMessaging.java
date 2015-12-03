package com.sports.unity.messages.controller.model;

import android.content.Context;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.privacy.PrivacyList;
import org.jivesoftware.smackx.privacy.PrivacyListManager;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by madmachines on 16/10/15.
 */
public class PersonalMessaging {

    private static final String PRIVACY_LIST_NAME = "spuBlockedList";

    private static PersonalMessaging pmessaging = null;

    synchronized public static PersonalMessaging getInstance(Context context) {
        if (pmessaging == null) {
            pmessaging = new PersonalMessaging(context);
        }
        return pmessaging;
    }

    public static boolean sendActionToCorrespondingActivityListener(String key, int id, Object data) {
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

    private final Map<Chat, ChatState> chatStates = new WeakHashMap<Chat, ChatState>();

    private SportsUnityDBHelper sportsUnityDBHelper = null;

    private PersonalMessaging(Context context) {
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
    }

    public void sendTextMessage(String msg, Chat chat, String number, long chatId) {
        Message message = new Message();
        message.setBody(msg);

        String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());
        String stanzaId = sendMessage(message, chat, time, SportsUnityDBHelper.MIME_TYPE_TEXT);

        long messageId = sportsUnityDBHelper.addMessage(message.getBody(), SportsUnityDBHelper.MIME_TYPE_TEXT, number, true, time,
                stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
        sportsUnityDBHelper.updateChatEntry(messageId, chatId, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
    }

    public void sendStickerMessage(String msg, Chat chat, String number, long chatId) {
        Message message = new Message();
        message.setBody(msg);

        String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());
        String stanzaId = sendMessage(message, chat, time, SportsUnityDBHelper.MIME_TYPE_STICKER);

        long messageId = sportsUnityDBHelper.addMessage(message.getBody(), SportsUnityDBHelper.MIME_TYPE_STICKER, number, true, time,
                stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
        sportsUnityDBHelper.updateChatEntry(messageId, chatId, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
    }

    public void sendMediaMessage(String checksum, Chat chat, long messageId, String mimeType) {
        Message message = new Message();
        message.setBody(checksum);

        long time = CommonUtil.getCurrentGMTTimeInEpoch();
        String stanzaId = sendMessage(message, chat, String.valueOf(time), mimeType);

        sportsUnityDBHelper.updateMediaMessage_ContentUploaded( messageId, stanzaId, checksum);
    }

    private String sendMessage(Message message, Chat chat, String currentTime, String mimeType){
        JivePropertiesManager.addProperty(message, Constants.PARAM_TIME, currentTime);
        JivePropertiesManager.addProperty(message, Constants.PARAM_MIME_TYPE, mimeType);

        DeliveryReceiptRequest.addTo(message);
        String id = message.getStanzaId();
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void sendStatus(ChatState newState, Chat chat) {

        if (chat == null || newState == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

        if (!updateChatState(chat, newState)) {
            return;
        }

        Message message = new Message();
        long time = CommonUtil.getCurrentGMTTimeInEpoch();
        JivePropertiesManager.addProperty(message, Constants.PARAM_TIME, time);
        ChatStateExtension extension = new ChatStateExtension(newState);
        message.addExtension(extension);
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void getLastTime(String jid) {
        Message msg = new Message("gettimedev@mm.io", Message.Type.headline);
        msg.setBody(jid);
        try {
            XMPPClient.getConnection().sendPacket(msg);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void updateBlockList(Context context){
        List<PrivacyItem> privacyItems = getPrivacyList();

        if( privacyItems.size() > -1 ) {
            ArrayList<String> blockedUserList = SportsUnityDBHelper.getInstance(context).getUserBlockedList();
            if( blockedUserList.size() != 0 ) {
                privacyItems = new ArrayList<>();
                for (String phoneNumber : blockedUserList ) {
                    phoneNumber += "@mm.io";
                    privacyItems.add( new PrivacyItem(PrivacyItem.Type.jid, phoneNumber, false, 1));
                    Log.i("Privacy" , phoneNumber);
                }
                sendPrivacyList(privacyItems);
            } else {
                //nothing
            }

        } else {
            //nothing
        }
    }

    public boolean changeUserBlockStatus(String phoneNumber, boolean status) {
        boolean success = false;
        phoneNumber += "@mm.io";

        List<PrivacyItem> privacyItems = getPrivacyList();

        /*
         * remove item if already exist.
         */
        for (PrivacyItem item : privacyItems) {
            String from = item.getValue();
            if (from.equalsIgnoreCase(phoneNumber)) {
                privacyItems.remove(item);
                break;
            }
        }

        /*
         * add new privacy item
         */
        PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid, phoneNumber, !status, 1);
        privacyItems.add(item);

        success = sendPrivacyList(privacyItems);

        return success;
    }

    private List<PrivacyItem> getPrivacyList(){
        List<PrivacyItem> privacyItems = new ArrayList<>();

        PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(XMPPClient.getConnection());
        try {
            PrivacyList plist = privacyManager.getPrivacyList(PRIVACY_LIST_NAME);
            privacyItems = plist.getItems();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

        return privacyItems;
    }

    private boolean sendPrivacyList(List<PrivacyItem> privacyItems){
        boolean success = false;
        PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(XMPPClient.getConnection());

        try{
            privacyManager.updatePrivacyList(PRIVACY_LIST_NAME, privacyItems);
            privacyManager.setActiveListName(PRIVACY_LIST_NAME);

            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    public void setReceivedReceipts(String fromJid, String receiptId, Context applicationContext) {

        /**
         * set read receipts in database
         */

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

        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, 0, null);
    }

}

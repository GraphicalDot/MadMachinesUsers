package com.sports.unity.messages.controller.model;

import android.content.Context;

import com.sports.unity.Database.SportsUnityDBHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.joda.time.DateTime;

/**
 * Created by madmachines on 27/10/15.
 */
public class GroupMessaging {

    private static GroupMessaging gmessaging = null;

    private Context context;

    private GroupMessaging(Context context) {

        this.context = context;

    }

    synchronized public static GroupMessaging getInstance(Context context) {
        if (gmessaging == null) {
            gmessaging = new GroupMessaging(context);
        }
        return gmessaging;
    }

    public void sendMessageToGroup(String msg, MultiUserChat multiUserChat) {
        Message message = new Message();
        message.setType(Message.Type.groupchat);
        message.setBody(msg);
        DateTime dateTime = DateTime.now();
        JivePropertiesManager.addProperty(message, "time", dateTime.getMillis());
        JivePropertiesManager.addProperty(message, "isGroupChat", "T");
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


    }

}

package com.sports.unity.messages.controller.model;

import android.content.Context;

import com.sports.unity.Database.SportsUnityDBHelper;

import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;

/**
 * Created by amandeep on 30/5/16.
 */
public class XMPPMessageQueueHelper {

    private static XMPPMessageQueueHelper XMPP_MESSAGE_QUEUE_HELPER = null;

    synchronized public static XMPPMessageQueueHelper getInstance() {
        if( XMPP_MESSAGE_QUEUE_HELPER == null ){
            XMPP_MESSAGE_QUEUE_HELPER = new XMPPMessageQueueHelper();
        }
        return XMPP_MESSAGE_QUEUE_HELPER;
    }

    private ArrayList<String> messagesQueued_In_XMPPConnection = new ArrayList<>();

    private XMPPMessageQueueHelper(){

    }

    public void sendPendingMessages(Context context, XMPPConnection connection){
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        ArrayList<Message> pendingMessages = sportsUnityDBHelper.getPendingMessages();

        for(Message message : pendingMessages){
            sendMessage(context, connection, message);
        }
    }

    public void clearMessageQueue(){
        messagesQueued_In_XMPPConnection.clear();
    }

    public void enqueue(String stanzaId){
        if( ! messagesQueued_In_XMPPConnection.contains(stanzaId) ) {
            messagesQueued_In_XMPPConnection.add(stanzaId);
        } else {
            //nothing
        }
    }

    public void dequeue(String stanzaId){
        messagesQueued_In_XMPPConnection.remove(stanzaId);
    }

    private void sendMessage(Context context, XMPPConnection connection, Message message){
        boolean retry = false;
        if( message.messageStanzaId == null ){
            retry = true;
        } else {
            if ( ! messagesQueued_In_XMPPConnection.contains(message.messageStanzaId) ) {
                retry = true;
            } else {
                //nothing
            }
        }

        if( retry ){
            SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
            boolean isGroupMessage = sportsUnityDBHelper.isGroupEntry(message.contactID);
            if (isGroupMessage) {
                PubSubMessaging.getInstance().resendMessage(context, message);
            } else {
                PersonalMessaging.getInstance(context).resendMessage(connection, message);
            }
        }
    }

}

package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 16/11/15.
 */
public class Chats {

    public int id;
    public String jid;
    public String name;
    public byte[] image;
    public int unreadCount;
    public int lastMessage;
    public boolean mute;

    public String data;
    public String mimeType;
    public String sent;
    public String received;

    public boolean block;
    public boolean isGroupChat;

    public Chats(int unread, String name, int id, int lastMessageId, String dataText,
                 String dataType, String sentTime, String recieveTime, String jid, byte[] image, boolean muteValue, boolean blockValue, boolean isGroupChat) {

        this.id = id;
        this.jid = jid;
        this.name = name;
        this.image = image;

        this.unreadCount = unread;
        this.lastMessage = lastMessageId;

        this.data = dataText;
        this.mimeType = dataType;
        this.sent = sentTime;
        this.received = recieveTime;

        this.mute = muteValue;
        this.block = blockValue;
        this.isGroupChat = isGroupChat;
    }

}


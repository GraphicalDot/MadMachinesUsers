package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 16/11/15.
 */
public class Chats {

    public int chatid;
    public String groupServerId = null;
    public int contactId;
    public String name;
    public byte[] chatImage;
    public int unreadCount;
    public int lastMessage;
    public boolean mute;

    public String data;
    public byte[] media;
    public String mimeType;
    public String sent;
    public String received;

    public byte[] userImage;
    public boolean block;

    public Chats(int unread, String name, int contactId, int lastMessageId, String dataText, byte[] dataMedia,
                 String dataType, String sentTime, String recieveTime, int chatId, String groupServerId, byte[] chatImage, boolean muteValue, byte[] userImage, boolean blockValue) {

        this.unreadCount = unread;
        this.name = name;
        this.contactId = contactId;
        this.lastMessage = lastMessageId;
        this.data = dataText;
        this.media = dataMedia;
        this.mimeType = dataType;
        this.sent = sentTime;
        this.received = recieveTime;
        this.userImage = userImage;
        this.chatid = chatId;

        this.groupServerId = groupServerId;
        this.chatImage = chatImage;

        this.mute = muteValue;

        this.block = blockValue;
    }

}


package com.sports.unity.messages.controller.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madmachines on 16/11/15.
 */
public class Chats implements Parcelable{

    public int chatid;
    public String groupServerId = null;
    public String name;
    public byte[] userImage;
    public byte[] groupImage;

    public int contactId;
    public int unreadCount;
    public int lastMessage;

    public String data;
    public byte[] media;
    public String mimeType;
    public String sent;
    public String recieved;

    public boolean mute;
    public boolean block;

    public Chats(int unread, String name, int contactId, int lastMessageId, String dataText, byte[] dataMedia,
                 String dataType, String sentTime, String recieveTime, int chatId, String groupServerId, byte[] groupImage, boolean muteValue, byte[] userImage, boolean blockValue) {

        this.unreadCount = unread;
        this.name = name;
        this.contactId = contactId;
        this.lastMessage = lastMessageId;
        this.data = dataText;
        this.media = dataMedia;
        this.mimeType = dataType;
        this.sent = sentTime;
        this.recieved = recieveTime;
        this.userImage = userImage;
        this.chatid = chatId;

        this.groupServerId = groupServerId;
        this.groupImage = groupImage;

        this.mute = muteValue;

        this.block = blockValue;
    }

    protected Chats(Parcel in) {
        chatid = in.readInt();
        groupServerId = in.readString();
        name = in.readString();
        userImage = in.createByteArray();
        groupImage = in.createByteArray();
        contactId = in.readInt();
        unreadCount = in.readInt();
        lastMessage = in.readInt();
        data = in.readString();
        media = in.createByteArray();
        mimeType = in.readString();
        sent = in.readString();
        recieved = in.readString();
    }

    public static final Creator<Chats> CREATOR = new Creator<Chats>() {
        @Override
        public Chats createFromParcel(Parcel in) {
            return new Chats(in);
        }

        @Override
        public Chats[] newArray(int size) {
            return new Chats[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chatid);
        dest.writeString(groupServerId);
        dest.writeString(name);
        dest.writeByteArray(userImage);
        dest.writeByteArray(groupImage);
        dest.writeInt(contactId);
        dest.writeInt(unreadCount);
        dest.writeInt(lastMessage);
        dest.writeString(data);
        dest.writeByteArray(media);
        dest.writeString(mimeType);
        dest.writeString(sent);
        dest.writeString(recieved);
    }
}


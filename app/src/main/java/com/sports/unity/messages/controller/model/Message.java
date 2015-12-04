package com.sports.unity.messages.controller.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sports.unity.Database.SportsUnityContract;

import java.util.ArrayList;

/**
 * Created by madmachines on 16/11/15.
 */
public class Message {

    public int id;
    public String number;
    public String textData;
    public byte[] media;
    public String mimeType;
    public String serverR;
    public String recipientR;
    public boolean iAmSender;
    public String sendTime;
    public String recieveTime;
    public boolean messagesRead;
    int contactID;
    public String mediaFileName;

    public Message(int id, String number, String textData, byte[] blob, String mimeType, String serverReceipt, String recipientReceipt,
                   Boolean iamsender, String recievetime, String sendtime, boolean read, int contactId, String mediaFileName) {
        this.id = id;
        this.number = number;
        this.textData = textData;
        this.media = blob;
        this.mimeType = mimeType;
        this.serverR = serverReceipt;
        this.recipientR = recipientReceipt;
        this.iAmSender = iamsender;
        this.sendTime = sendtime;
        this.recieveTime = recievetime;
        this.messagesRead = read;
        this.contactID = contactId;
        this.mediaFileName = mediaFileName;
    }

}

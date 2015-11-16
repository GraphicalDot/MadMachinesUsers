package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 16/11/15.
 */
public class Contacts {

    public String name;
    public String jid;
    public boolean registered;
    public byte[] image;
    public String status;
    public long id;

    public Contacts(String name, String phoneNumber, Boolean registered, byte[] userimage, long cId, String status) {
        this.name = name;
        this.jid = phoneNumber;
        this.registered = registered;
        this.image = userimage;
        this.status = status;
        this.id = cId;
    }

}

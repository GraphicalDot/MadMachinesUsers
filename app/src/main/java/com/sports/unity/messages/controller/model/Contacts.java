package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 16/11/15.
 */
public class Contacts {

    public long id;
    public String name;
    public String jid;
    public boolean registered;
    public byte[] image;
    public String status;

    public Contacts(String name, String phoneNumber, Boolean registered, byte[] userImage, long cId, String status) {
        this.name = name;
        this.jid = phoneNumber;
        this.registered = registered;
        this.image = userImage;
        this.status = status;
        this.id = cId;
    }

    @Override
    public String toString() {
        return name;
    }

}

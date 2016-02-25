package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 16/11/15.
 */
public class Contacts {

    public long id;
    public String jid;
    public String name;
    public String phoneNumber;
    public byte[] image;
    public String status;

    public Contacts(String name, String jid, String phoneNumber, byte[] userImage, long cId, String status) {
        this.name = name;
        this.jid = jid;
        this.phoneNumber = phoneNumber;
        this.image = userImage;
        this.status = status;
        this.id = cId;
    }

    public boolean isRegistered(){
        return jid != null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        String s1 = String.valueOf(id);
        String s2 = String.valueOf(((Contacts) o).id);
        return s1.equals(s2);
    }

}

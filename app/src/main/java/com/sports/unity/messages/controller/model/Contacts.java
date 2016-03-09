package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 16/11/15.
 */
public class Contacts {

    public static final int AVAILABLE_NOT = 0;
    public static final int AVAILABLE_BY_PEOPLE_AROUND_ME = 1;
    public static final int AVAILABLE_BY_OTHER_CONTACTS = 2;
    public static final int AVAILABLE_BY_MY_CONTACTS = 3;

    public long id;
    public String jid;
    public String name;
    public String phoneNumber;
    public byte[] image;
    public String status;
    public int availableStatus = AVAILABLE_NOT;

    public Contacts(String name, String jid, String phoneNumber, byte[] userImage, long cId, String status, int availableStatus) {
        this.name = name;
        this.jid = jid;
        this.phoneNumber = phoneNumber;
        this.image = userImage;
        this.status = status;
        this.id = cId;
        this.availableStatus = availableStatus;
    }

    public boolean isAvailable(){
        return availableStatus != AVAILABLE_NOT;
    }

    public boolean isOthers(){
        return availableStatus < AVAILABLE_BY_MY_CONTACTS;
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

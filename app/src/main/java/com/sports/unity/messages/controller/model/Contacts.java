package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 16/11/15.
 */
public class Contacts {

    public static final int AVAILABLE_NOT = 0;
    public static final int AVAILABLE_BY_PEOPLE_AROUND_ME = 1;
    public static final int AVAILABLE_BY_OTHER_CONTACTS = 2;
    public static final int AVAILABLE_BY_MY_CONTACTS = 3;

    public static final int DEFAULT_PENDNG_REQUEST_ID = 0;
    public static final int WAITING_FOR_REQUEST_ACCEPTANCE = 1;
    public static final int PENDING_REQUESTS_TO_PROCESS = 2;
    public static final int REQUEST_ACCEPTED = 3;
    public static final int REQUEST_BLOCKED = 4;

    public int id;
    public String jid;
    private String name;
    public String phoneNumber;
    public byte[] image;
    public String status;
    public int availableStatus = AVAILABLE_NOT;
    public int requestStatus = DEFAULT_PENDNG_REQUEST_ID;
    public boolean blockStatus = false;

    public Contacts(String name, String jid, String phoneNumber, byte[] userImage, int cId, String status, int availableStatus) {
        this.name = name;
        this.jid = jid;
        this.phoneNumber = phoneNumber;
        this.image = userImage;
        this.status = status;
        this.id = cId;
        this.availableStatus = availableStatus;
    }

    public Contacts(String name, String jid, String phoneNumber, byte[] userImage, int cId, String status, int availableStatus, boolean blockStatus) {
        this.name = name;
        this.jid = jid;
        this.phoneNumber = phoneNumber;
        this.image = userImage;
        this.status = status;
        this.id = cId;
        this.availableStatus = availableStatus;
        this.blockStatus = blockStatus;
    }

    public Contacts(String name, String jid, String phoneNumber, byte[] userImage, int cId, String status, int availableStatus, int requestStatus, boolean blockStatus) {
        this.name = name;
        this.jid = jid;
        this.phoneNumber = phoneNumber;
        this.image = userImage;
        this.status = status;
        this.id = cId;
        this.availableStatus = availableStatus;
        this.requestStatus = requestStatus;
        this.blockStatus = blockStatus;
    }

    public boolean isAvailable() {
        return availableStatus != AVAILABLE_NOT;
    }

    public boolean isOthers() {
        return availableStatus < AVAILABLE_BY_MY_CONTACTS;
    }

    public boolean isRegistered() {
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

    public String getName() {
        if (name == null || name.isEmpty()) {
            name = "unknown";
        }
        return name;
    }

}

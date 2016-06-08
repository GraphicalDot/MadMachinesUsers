package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 12/5/16.
 */
public class User implements Comparable<User> {

    private String name;
    private String jid;
    private int distance;
    private String lastSeen;
    private boolean isOnline = false;

    public User(String name, String jid, int distance, String lastSeen, boolean isOnline) {
        this.name = name;
        this.jid = jid;
        this.distance = distance;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }

    public String getName() {
        if (name == null) {
            name = "unknown";
        }
        return name;
    }

    public String getJid() {
        return jid;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isUserOnline() {
        return isOnline;
    }

    public String getLastSeen() {
        return lastSeen;
    }


    @Override
    public int compareTo(User another) {
        int value = 0;
        if (isUserOnline() == another.isUserOnline()) {
            value = Integer.compare(getDistance(), another.getDistance());
        } else {
            value = Boolean.compare(another.isUserOnline(), this.isUserOnline());
        }
        return value;
    }
}


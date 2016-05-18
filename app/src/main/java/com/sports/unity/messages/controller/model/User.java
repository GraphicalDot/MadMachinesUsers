package com.sports.unity.messages.controller.model;

/**
 * Created by madmachines on 12/5/16.
 */
public class User {

    private String name;
    private String jid;
    private int distance;

    public User(String name, String jid, int distance) {
        this.name = name;
        this.jid = jid;
        this.distance = distance;
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
}


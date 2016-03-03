package com.sports.unity.messages.controller.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manish on 02/03/16.
 */
public class Person implements ClusterItem {
    private String username;
    private double distance;
    private LatLng position;
    private boolean friend;
    private boolean commonInterest;
    private List<String> interests = new ArrayList<>();

    public List<String> getInterests() {
        return interests;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public boolean isCommonInterest() {
        return commonInterest;
    }

    public void setCommonInterest(boolean commonInterest) {
        this.commonInterest = commonInterest;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}

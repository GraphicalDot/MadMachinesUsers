package com.sports.unity.peoplearound.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeDTO {
    private String username;
    private double distance;
    private LatLng position;
    private boolean friend;
    private boolean commonInterest;

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

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
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

}

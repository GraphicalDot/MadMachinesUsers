package com.sports.unity.messages.controller.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manish on 02/03/16.
 */
public class Person {
    private String username;
    private double distance;
    private double longitude;
    private double latitude;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

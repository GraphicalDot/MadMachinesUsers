package com.sports.unity.messages.controller.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by manish on 02/03/16.
 */
public class Person implements  Parcelable, ClusterItem ,Comparable<Person>{
    private String username;
    private Integer distance;
    private LatLng position;
    private boolean friend;
    private boolean commonInterest;
    private String name;
    private List<String> interests = new ArrayList<>();
    public Person(){

    }
    protected Person(Parcel in) {
        username = in.readString();
        distance = in.readInt();
        position = in.readParcelable(LatLng.class.getClassLoader());
        friend = in.readByte() != 0;
        commonInterest = in.readByte() != 0;
        interests = in.createStringArrayList();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public List<String> getInterests() {
        return interests;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
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

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeDouble(distance);
        dest.writeParcelable(position, flags);
        dest.writeByte((byte) (friend ? 1 : 0));
        dest.writeByte((byte) (commonInterest ? 1 : 0));
        dest.writeStringList(interests);
    }

    @Override
    public int compareTo(Person another) {
        return another.getDistance().compareTo(this.getDistance());
    }
}

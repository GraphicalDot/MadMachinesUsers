package com.sports.unity.messages.controller.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manish on 02/03/16.
 */
public class PeoplesNearMe {
    List<Person> friendsNearMe = new ArrayList<>();
    List<Person> anonymousPeoples = new ArrayList<>();

    public List<Person> getFriendsNearMe() {
        return friendsNearMe;
    }

    public List<Person> getAnonymousPeoples() {
        return anonymousPeoples;
    }
}

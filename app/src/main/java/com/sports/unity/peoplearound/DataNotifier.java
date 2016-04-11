package com.sports.unity.peoplearound;

import com.sports.unity.messages.controller.model.Person;

import java.util.ArrayList;

/**
 * Created by madmachines on 11/4/16.
 */
public interface DataNotifier {
    void notifyPeoples(ArrayList<Person> peoples);
}

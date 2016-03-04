package com.sports.unity.messages.controller.activity;

import com.google.maps.android.clustering.Cluster;
import com.sports.unity.messages.controller.model.Person;

/**
 * Created by manish on 03/03/16.
 */
public interface PeopleService {
    boolean showProfile(Person person);

    void showCluster(Cluster<Person> cluster);
}

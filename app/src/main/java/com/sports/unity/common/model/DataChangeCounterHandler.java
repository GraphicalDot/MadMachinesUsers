package com.sports.unity.common.model;

import java.util.HashMap;

/**
 * Created by madmachines on 2/6/16.
 */
public class DataChangeCounterHandler {

    private int counter = 0;
    private HashMap<String, Integer> mapOfCounter = new HashMap<>();

    public DataChangeCounterHandler() {

    }

    public void setContentCounter(String key) {
        mapOfCounter.put(key, counter);
    }

    public boolean isContentChanged(String key) {
        boolean changed = false;
        if (mapOfCounter.containsKey(key)) {
            int value = mapOfCounter.get(key);
            if (value != counter) {
                changed = true;
            }
        }
        return changed;
    }

    public void contentChanged() {
        counter++;
    }

}

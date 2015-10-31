package com.sports.unity.util;

import java.util.HashMap;

/**
 * Created by madmachines on 29/10/15.
 */
public class ActivityActionHandler {

    public static final String CHAT_SCREEN_KEY = "chat_screen_key";
    public static final String CHAT_LIST_KEY = "chat_list_key";

    private static ActivityActionHandler activityActionHandler = null;

    public static ActivityActionHandler getInstance(){
        if( activityActionHandler == null ){
            activityActionHandler = new ActivityActionHandler();
        }
        return activityActionHandler;
    }

    private HashMap<String, ActivityActionListener> activityListenerMap = new HashMap<>();

    private ActivityActionHandler(){

    }

    public void addActionListener(String key, ActivityActionListener listener){
        activityListenerMap.put(key, listener);
    }

    public void removeActionListener(String key){
        activityListenerMap.remove(key);
    }

    public ActivityActionListener getActionListener(String key){
        return activityListenerMap.get(key);
    }

}

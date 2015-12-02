package com.sports.unity.util;

import java.util.HashMap;
import java.util.Objects;

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
    private HashMap<String, ActionItem> actionItemOnHoldMap = new HashMap<>();

    private ActivityActionHandler(){

    }

    public void addActionListener(String key, ActivityActionListener listener){
        activityListenerMap.put(key, listener);

        if( actionItemOnHoldMap.containsKey(key) ){
            ActionItem actionItem = actionItemOnHoldMap.get(key);
            listener.handleMediaContent( actionItem.getMimeType(), actionItem.getContent());
            actionItemOnHoldMap.remove(key);
        }
    }

    public void removeActionListener(String key){
        activityListenerMap.remove(key);
    }

    public ActivityActionListener getActionListener(String key){
        return activityListenerMap.get(key);
    }

    public void addActionOnHold(String key, ActionItem actionItem){
        actionItemOnHoldMap.put( key, actionItem);
    }

    public static class ActionItem {

        private String mimeType = null;
        private Object content = null;


        public ActionItem(String mimeType, Object content){
            this.mimeType = mimeType;
            this.content = content;
        }

        public Object getContent() {
            return content;
        }

        public String getMimeType() {
            return mimeType;
        }

    }

}

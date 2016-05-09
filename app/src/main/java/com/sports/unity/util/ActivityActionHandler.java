package com.sports.unity.util;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Filter;

/**
 * Created by madmachines on 29/10/15.
 */
public class ActivityActionHandler {

    public static final String CHAT_SCREEN_KEY = "chat_screen_key";
    public static final String REQEUSTS_SCREEN_KEY = "requests_screen_key";
    public static final String USER_PROFILE_KEY = "user_profile_key";
    public static final String CHAT_LIST_KEY = "chat_list_key";
    public static final String CHAT_OTHERS_LIST_KEY = "chat_list_others_key";
    public static final String UNREAD_COUNT_KEY = "unread_count";

    public static final int EVENT_ID_COMMON = 0;
    public static final int EVENT_ID_SEND_MEDIA = 1;
    public static final int EVENT_ID_DOWNLOAD_COMPLETED = 2;
    public static final int EVENT_ID_INCOMING_MEDIA = 3;
    public static final int EVENT_ID_CHAT_STATUS = 4;
    public static final int EVENT_ID_RECEIPT = 5;
    public static final int EVENT_FRIEND_REQUEST_SENT = 6;
    public static final int EVENT_FRIEND_REQUEST_RECEIVED = 7;
    public static final int EVENT_FRIEND_REQUEST_ACCEPTED = 8;

    private static ActivityActionHandler activityActionHandler = null;

    public static ActivityActionHandler getInstance() {
        if (activityActionHandler == null) {
            activityActionHandler = new ActivityActionHandler();
        }
        return activityActionHandler;
    }

    private HashMap<String, ActivityActionListener> activityListenerMap = new HashMap<>();
    private HashMap<String, ActionItem> actionItemOnHoldMap = new HashMap<>();
    private HashMap<String, String> filterMap = new HashMap<>();

    private ActivityActionHandler() {

    }

    public void addActionListener(String key, ActivityActionListener listener) {
        activityListenerMap.put(key, listener);

        if (actionItemOnHoldMap.containsKey(key)) {
            ActionItem actionItem = actionItemOnHoldMap.get(key);
            listener.handleMediaContent(actionItem.id, actionItem.getMimeType(), actionItem.getMessageContent(), actionItem.getThumbnailImage(), actionItem.getMediaContent());
            actionItemOnHoldMap.remove(key);
        }
    }

    public void addActionListener(String key, String filter, ActivityActionListener listener) {
        activityListenerMap.put(key, listener);

        boolean filterMatched = false;
        if (filterMap.containsKey(key) && filterMap.get(key).equals(filter)) {
            filterMatched = true;
        }

        if (filterMatched && actionItemOnHoldMap.containsKey(key)) {
            ActionItem actionItem = actionItemOnHoldMap.get(key);
            listener.handleMediaContent(actionItem.id, actionItem.getMimeType(), actionItem.getMessageContent(), actionItem.getThumbnailImage(), actionItem.getMediaContent());
            actionItemOnHoldMap.remove(key);
        }

        filterMap.put(key, filter);
    }

    public void removeActionListener(String key) {
        if (!filterMap.containsKey(key)) {
            activityListenerMap.remove(key);
        }
    }

    public void removeActionListener(String key, String filter) {
        if (filterMap.containsKey(key) && filterMap.get(key).equals(filter)) {
            activityListenerMap.remove(key);
        }
    }

    public ActivityActionListener getActionListener(String key) {
        return activityListenerMap.get(key);
    }

    public ActivityActionListener getActionListener(String key, String filter) {
        boolean filterMatched = false;
        if (filterMap.containsKey(key) && filterMap.get(key).equals(filter)) {
            filterMatched = true;
        }

        ActivityActionListener activityActionListener = null;
        if (filterMatched) {
            activityActionListener = activityListenerMap.get(key);
        } else {
            //nothing
        }

        return activityActionListener;
    }

    public void addActionOnHold(String key, ActionItem actionItem) {
        actionItemOnHoldMap.put(key, actionItem);
    }

    public boolean dispatchCommonEvent(String key) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleAction(EVENT_ID_COMMON);
            success = true;
        }
        return success;
    }

    public boolean dispatchCommonEvent(String key, String filter) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filter);

        if (actionListener != null) {
            actionListener.handleAction(EVENT_ID_COMMON);
            success = true;
        }
        return success;
    }

//    public boolean dispatchCommonEventWithData(String key, String filter, Object data) {
//        boolean success = false;
//
//        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
//        ActivityActionListener actionListener = null;//activityActionHandler.getActionListener(key, filter);
//
//        if (actionListener != null) {
//            if (data == null) {
//                actionListener.handleAction(EVENT_ID_COMMON);
//            } else {
//                actionListener.handleAction(EVENT_ID_COMMON, data);
//            }
//            success = true;
//        }
//        return success;
//    }

    public boolean dispatchReceiptEvent(String key, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filterMap.get(key));

        if (actionListener != null) {
            actionListener.handleAction(EVENT_ID_RECEIPT, data);
            success = true;
        }
        return success;
    }

    public boolean dispatchUserStatusOnChat(String key, String filter, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filter);

        if (actionListener != null) {
            actionListener.handleAction(EVENT_ID_CHAT_STATUS, data);
            success = true;
        }
        return success;
    }

    public boolean dispatchDownloadCompletedEvent(String key, String filter, String mimeType, String fileName, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filter);

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_DOWNLOAD_COMPLETED, mimeType, fileName, mediaContent);
            success = true;
        }
        return success;
    }

    public boolean dispatchIncomingMediaEvent(String key, String filter, String mimeType, String checksum, int messageId) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_INCOMING_MEDIA, mimeType, checksum, messageId);
            success = true;
        }
        return success;
    }

    public boolean dispatchRequestStatusEvent(String key, String filter, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filter);

        if (actionListener != null) {
            actionListener.handleAction(EVENT_FRIEND_REQUEST_SENT, data);
            success = true;
        }

        return success;
    }

    public boolean receivedRequestStatusEvent(String key, String filter, Object data, int EVENT) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filter);

        if (actionListener != null) {
            actionListener.handleAction(EVENT, data);
            success = true;
        }

        return success;
    }

    public boolean dispatchSendStickerEvent(String key, String mimeType, String stickerPath) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filterMap.get(key));

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_SEND_MEDIA, mimeType, stickerPath, null);
            success = true;
        }
        return success;
    }

    public boolean dispatchSendMediaEvent(String key, String mimeType, String fileName, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filterMap.get(key));

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_SEND_MEDIA, mimeType, fileName, mediaContent);
            success = true;
        }
        return success;
    }

    public boolean dispatchSendMediaEvent(String key, String mimeType, String fileName, String thumbnailImageAsBase64, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filterMap.get(key));

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_SEND_MEDIA, mimeType, fileName, thumbnailImageAsBase64, mediaContent);
            success = true;
        }
        return success;
    }

    public boolean pendingDispatchSendMediaEvent(String key, String mimeType, String fileName, String thumbnailImageAsBase64, Object bytes) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        activityActionHandler.addActionOnHold(key, new ActivityActionHandler.ActionItem(EVENT_ID_SEND_MEDIA, mimeType, fileName, thumbnailImageAsBase64, bytes));
        return success;
    }

    public boolean acceptRequestStatusEvent(String key, String filter, String data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key, filter);

        if (actionListener != null) {
            actionListener.handleAction(EVENT_FRIEND_REQUEST_ACCEPTED, data);
            success = true;
        }

        return success;
    }

    public static class ActionItem {

        private int id = 0;
        private String mimeType = null;
        private Object messageContent = null;
        private String thumbnailImage = null;
        private Object mediaContent = null;


        public ActionItem(int id, String mimeType, Object messageContent, String thumbnailImage, Object mediaContent) {
            this.id = id;
            this.mimeType = mimeType;
            this.messageContent = messageContent;
            this.thumbnailImage = thumbnailImage;
            this.mediaContent = mediaContent;
        }

        public int getId() {
            return id;
        }

        public Object getMediaContent() {
            return mediaContent;
        }

        public Object getMessageContent() {
            return messageContent;
        }

        public String getThumbnailImage() {
            return thumbnailImage;
        }

        public String getMimeType() {
            return mimeType;
        }

    }

}

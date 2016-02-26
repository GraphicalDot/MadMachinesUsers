package com.sports.unity.util;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by madmachines on 29/10/15.
 */
public class ActivityActionHandler {

    public static final String CHAT_SCREEN_KEY = "chat_screen_key";
    public static final String CHAT_LIST_KEY = "chat_list_key";
    public static final String CHAT_OTHERS_LIST_KEY = "chat_list_others_key";
    public static final String UNREAD_COUNT_KEY = "unread_count";

    public static final int EVENT_ID_COMMON = 0;
    public static final int EVENT_ID_SEND_MEDIA = 1;
    public static final int EVENT_ID_DOWNLOAD_COMPLETED = 2;
    public static final int EVENT_ID_INCOMING_MEDIA = 3;
    public static final int EVENT_ID_CHAT_STATUS = 4;
    public static final int EVENT_ID_RECEIPT = 5;

    private static ActivityActionHandler activityActionHandler = null;

    public static ActivityActionHandler getInstance() {
        if (activityActionHandler == null) {
            activityActionHandler = new ActivityActionHandler();
        }
        return activityActionHandler;
    }

    private HashMap<String, ActivityActionListener> activityListenerMap = new HashMap<>();
    private HashMap<String, ActionItem> actionItemOnHoldMap = new HashMap<>();

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

    public void removeActionListener(String key) {
        activityListenerMap.remove(key);
    }

    public ActivityActionListener getActionListener(String key) {
        return activityListenerMap.get(key);
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

    public boolean dispatchCommonEvent(String key, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            if (data == null) {
                actionListener.handleAction(EVENT_ID_COMMON);
            } else {
                actionListener.handleAction(EVENT_ID_COMMON, data);
            }
            success = true;
        }
        return success;
    }

    public boolean dispatchReceiptEvent(String key, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleAction(EVENT_ID_RECEIPT, data);
            success = true;
        }
        return success;
    }

    public boolean dispatchUserStatusOnChat(String key, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleAction(EVENT_ID_CHAT_STATUS, data);
            success = true;
        }
        return success;
    }

    public boolean dispatchSendStickerEvent(String key, String mimeType, String stickerPath) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_SEND_MEDIA, mimeType, stickerPath, null);
            success = true;
        }
        return success;
    }

    public boolean dispatchDownloadCompletedEvent(String key, String mimeType, String fileName, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_DOWNLOAD_COMPLETED, mimeType, fileName, mediaContent);
            success = true;
        }
        return success;
    }

    public boolean dispatchIncomingMediaEvent(String key, String mimeType, String checksum, long messageId) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_INCOMING_MEDIA, mimeType, checksum, messageId);
            success = true;
        }
        return success;
    }

    public boolean dispatchSendMediaEvent(String key, String mimeType, String fileName, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent(EVENT_ID_SEND_MEDIA, mimeType, fileName, mediaContent);
            success = true;
        }
        return success;
    }

    public boolean dispatchSendMediaEvent(String key, String mimeType, String fileName, String thumbnailImageAsBase64, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

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

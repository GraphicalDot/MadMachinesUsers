package com.sports.unity.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by madmachines on 8/12/15.
 */
public class NotificationHandler {

    public static final String OTHERS_COUNT = "others_count";
    public static final String CHAT_MESSAGE_COUNT_SET = "chat_message_count_set";

    public static final int NOTIFICATION_ID = 1;
    public static final int MESSAGE_LIMIT = 5;

    private static NotificationHandler NOTIFICAION_HANDLER = null;

    public static NotificationHandler getInstance(Context context) {
        if (NOTIFICAION_HANDLER == null) {
            NOTIFICAION_HANDLER = new NotificationHandler(context);
        }
        return NOTIFICAION_HANDLER;
    }

    public static void dismissNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private Context context;
    private ArrayList<NotificationMessage> notificationMessageList = new ArrayList<>();
    private HashMap<String, Integer> chatIdAndMessageCountMap = new HashMap<>();

    private int unreadMessageCount = 0;
    private int friendsChatCount = 0;
    private int othersChatCount = 0;

    public NotificationHandler(Context context) {
        this.context = context;
        initUnreadCountData(context);
    }

    synchronized public void addNotificationMessage(long chatId, String from, String message, String mimeType, byte[] image) {
        NotificationMessage notificationMessage = new NotificationMessage(chatId, from, message, mimeType, image);
        notificationMessageList.add(notificationMessage);

        updateUnreadCountBasedOnNewMessageArrived(String.valueOf(chatId));
//        updateUnreadCount(chatIdSet.size(), 0, notificationMessageList.size());

        if (notificationMessageList.size() > MESSAGE_LIMIT) {
            while (notificationMessageList.size() > MESSAGE_LIMIT) {
                notificationMessageList.remove(0);
            }
        } else {
            //nothing
        }
    }

    public int getNotificationChatCount() {
        return chatIdAndMessageCountMap.size();
    }

    public int getNotificationMessagesCount() {
        return unreadMessageCount;
    }

    public void showNotification(Context context, PendingIntent pendingIntent) {
        int chatCount = getNotificationChatCount();
        int messageCount = getNotificationMessagesCount();

        if (messageCount > 0) {
            NotificationMessage message = notificationMessageList.get(notificationMessageList.size() - 1);

            if (chatCount == 1 && messageCount == 1) {
                singleMessageNotification(context, pendingIntent, message);
            } else {
                comboMessageNotification(context, pendingIntent, message, chatCount, messageCount);
            }
        }

    }

    synchronized public void clearNotificationMessages(String chatId) {
        int messageCount = 0;
        if (chatIdAndMessageCountMap.containsKey(chatId)) {
            messageCount = chatIdAndMessageCountMap.get(chatId);
        }
        chatIdAndMessageCountMap.remove(chatId);

        updateUnreadCountBasedOnChatViewed(messageCount);

        for (int index = 0; index < notificationMessageList.size(); index++) {
            if (notificationMessageList.get(index).chatId == Long.parseLong(chatId)) {
                notificationMessageList.remove(index);
                index--;
            }
        }
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public int getUnreadFriendsChatCount() {
        return friendsChatCount;
    }

    public int getUnreadOthersChatCount() {
        return othersChatCount;
    }

    private String getUnreadCountData(Context context) {
        String jsonData = TinyDB.getInstance(context).getString(TinyDB.UNREAD_NOTIFICATION_COUNT_KEY);
        return jsonData;
    }

    private void updateUnreadCountBasedOnNewMessageArrived(String chatId) {
        unreadMessageCount++;

        int messageCount = 0;
        if (chatIdAndMessageCountMap.containsKey(chatId)) {
            messageCount = chatIdAndMessageCountMap.get(chatId);
        }
        messageCount++;

        chatIdAndMessageCountMap.put(chatId, messageCount);
        friendsChatCount = chatIdAndMessageCountMap.size();

        String data = createUnreadCountJson();
        setUnreadCountData(data, context);
    }

    private void updateUnreadCountBasedOnChatViewed(int messageCount) {
        unreadMessageCount -= messageCount;
        friendsChatCount = chatIdAndMessageCountMap.size();

        String data = createUnreadCountJson();
        setUnreadCountData(data, context);
    }

    private void setUnreadCountData(String jsonData, Context context) {
        TinyDB.getInstance(context).putString(TinyDB.UNREAD_NOTIFICATION_COUNT_KEY, jsonData);
    }

    private String createUnreadCountJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OTHERS_COUNT, othersChatCount);

            JSONObject chatMessageCount = new JSONObject();
            Iterator<String> iterator = chatIdAndMessageCountMap.keySet().iterator();
            while (iterator.hasNext()) {
                String chatId = iterator.next();
                Integer messageCount = chatIdAndMessageCountMap.get(chatId);
                chatMessageCount.put(chatId, messageCount);
            }
            jsonObject.put(CHAT_MESSAGE_COUNT_SET, chatMessageCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    synchronized private void initUnreadCountData(Context context) {
        String data = getUnreadCountData(context);
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                othersChatCount = jsonObject.getInt(OTHERS_COUNT);

                JSONObject chatMessageCount = jsonObject.getJSONObject(CHAT_MESSAGE_COUNT_SET);
                Iterator<String> keys = chatMessageCount.keys();
                while (keys.hasNext()) {
                    String key = keys.next();

                    Integer count = chatMessageCount.getInt(key);
                    chatIdAndMessageCountMap.put(key, count);

                    unreadMessageCount += count;
                }

                friendsChatCount = chatIdAndMessageCountMap.size();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void singleMessageNotification(Context context, PendingIntent pendingIntent, NotificationMessage messageArrived) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setColor(context.getResources().getColor(R.color.app_theme_blue));

        if (messageArrived.getProfileImage() != null) {
            builder.setLargeIcon(getCroppedBitmap(messageArrived.getProfileImage()));
        } else {
            //nothing
        }

        if (UserUtil.isNotificationPreviews()) {
            builder.setContentText(messageArrived.getTitleMessage());
        } else {
            builder.setContentText("Message from " + messageArrived.from);
        }

        builder.setContentTitle(messageArrived.from);

        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_HIGH);
        int defaults = 0;
        defaults = getDefaults(context, defaults, builder);
        builder.setDefaults(defaults);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationHandler.NOTIFICATION_ID, builder.build());
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2)
                , (float) (height / 2)
                , (float) Math.min(height / 2, width / 2)
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    private void comboMessageNotification(Context context, PendingIntent pendingIntent, NotificationMessage messageArrived, int chatCount, int messageCount) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (UserUtil.isNotificationPreviews()) {
            builder.setSmallIcon(R.drawable.ic_stat_notification);
            if (chatCount == 1) {
                if (messageArrived.getProfileImage() != null) {
                    builder.setLargeIcon(getCroppedBitmap(messageArrived.getProfileImage()));
                } else {
                    //nothing
                }
                builder.setContentTitle(messageArrived.from);
                if (messageCount == 1) {
                    builder.setContentText(messageArrived.getTitleMessage());
                } else {
                    builder.setContentText(String.valueOf(messageCount) + " new Messages ");
                }
            } else {
                builder.setContentTitle("Sports Unity");
                builder.setColor(context.getResources().getColor(R.color.app_theme_blue));
                builder.setContentText(String.valueOf(messageCount) + " Messages from " + String.valueOf(chatCount) + " chats");
            }
            setStyle(builder, chatCount, messageArrived, context, messageCount);
        } else {
            if (chatCount == 1) {
                builder.setContentTitle(messageArrived.from);
                if (messageArrived.getProfileImage() != null) {
                    builder.setLargeIcon(getCroppedBitmap(messageArrived.getProfileImage()));
                } else {
                    //nothing
                }
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                builder.setColor(context.getResources().getColor(R.color.app_theme_blue));
                if (messageCount == 1) {
                    builder.setContentText("Message from " + messageArrived.from);
                } else {
                    builder.setContentText(String.valueOf(messageCount) + " Messages from " + messageArrived.from);
                }
            } else {
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                builder.setContentTitle("Sports Unity");
                builder.setColor(context.getResources().getColor(R.color.app_theme_blue));

                builder.setContentText(String.valueOf(messageCount) + " Messages from " + String.valueOf(chatCount) + " chats");
            }
        }

        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setAutoCancel(true);

        int defaults = 0;
        defaults = getDefaults(context, defaults, builder);
        builder.setDefaults(defaults);

        Uri uri = Uri.parse(UserUtil.getNotificationSoundURI());
        builder.setSound(uri);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationHandler.NOTIFICATION_ID, builder.build());
    }

    private int getDefaults(Context context, int defaults, NotificationCompat.Builder builder) {
        if ( UserUtil.isConversationVibrate() ) {
            defaults = defaults | Notification.DEFAULT_VIBRATE;
        } else {
            long vibratePattern[] = new long[]{0l};
            builder.setVibrate(vibratePattern);
        }

//        if ( soundEnabled(context) ) {
//            defaults = defaults | Notification.DEFAULT_SOUND;
//        } else {
//            builder.setSound(null);
//        }

        if ( UserUtil.isNotificationLight() ) {
            builder.setLights(context.getResources().getColor(R.color.app_theme_blue), 100, 3000);
        } else {

        }
        return defaults;
    }

    private void setStyle(NotificationCompat.Builder builder, int chatCount, NotificationMessage messageArrived, Context context, int messageCount) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        if (chatCount == 1) {
            style.setBigContentTitle(messageArrived.from);

            if (messageArrived.getProfileImage() != null) {
                builder.setLargeIcon(getCroppedBitmap(messageArrived.getProfileImage()));
            } else {
                //nothing
            }
            builder.setSmallIcon(R.drawable.ic_stat_notification);
            builder.setColor(context.getResources().getColor(R.color.app_theme_blue));

            int index = notificationMessageList.size() - MESSAGE_LIMIT;
            if (index < 0) {
                index = 0;
            }
            for (; index < notificationMessageList.size(); index++) {
                style.addLine(notificationMessageList.get(index).getMessageLine(false));
            }

            style.setSummaryText(messageCount + " messages");
        } else {
            style.setBigContentTitle("Sports Unity");
            builder.setSmallIcon(R.drawable.ic_stat_notification);
            builder.setColor(context.getResources().getColor(R.color.app_theme_blue));

            int index = notificationMessageList.size() - MESSAGE_LIMIT;
            if (index < 0) {
                index = 0;
            }
            for (; index < notificationMessageList.size(); index++) {
                style.addLine(notificationMessageList.get(index).getMessageLine(true));
            }

            style.setSummaryText(messageCount + " messages from " + chatCount + " chats");
        }
        builder.setStyle(style);
    }


    private class NotificationMessage {
        private long chatId;
        private String from;
        private String message;
        private String mimeType;
        private byte[] image;

        NotificationMessage(long chatId, String from, String message, String mimeType, byte[] image) {
            this.chatId = chatId;
            this.from = from;
            this.message = message;
            this.mimeType = mimeType;
            this.image = image;
        }

        private Bitmap getProfileImage() {
            Bitmap profileImage = null;
            if (image != null) {
                profileImage = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            return profileImage;
        }

        private String getTitleMessage() {
            String message = null;
            if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
                message = this.message;
            } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
                message = "Voice message";
            } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
                message = "Image";
            } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
                message = "Video";
            } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
                message = "Sticker";
            }
            return message;
        }

        private String getMessageLine(boolean fromRequired) {
            String returnMessage = null;
            if (fromRequired) {
                returnMessage = from + " : " + getTitleMessage();
            } else {
                if (from.indexOf('@') > 0) {
                    returnMessage = from.substring(0, from.indexOf('@')) + " : " + getTitleMessage();
                } else {
                    returnMessage = getTitleMessage();
                }
            }
            return returnMessage;
        }

//        @Override
//        public String toString() {
//            return from + " : " + message;
//        }
    }

}

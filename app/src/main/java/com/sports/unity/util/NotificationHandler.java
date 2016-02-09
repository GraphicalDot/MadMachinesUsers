package com.sports.unity.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.SettingsActivity;
import com.sports.unity.common.model.TinyDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by madmachines on 8/12/15.
 */
public class NotificationHandler {

    public static final int NOTIFICATION_ID = 1;
    public static final int MESSAGE_LIMIT = 5;

    private static NotificationHandler NOTIFICAION_HANDLER = null;

    public static NotificationHandler getInstance() {
        if (NOTIFICAION_HANDLER == null) {
            NOTIFICAION_HANDLER = new NotificationHandler();
        }
        return NOTIFICAION_HANDLER;
    }

    public static void dismissNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private ArrayList<NotificationMessage> notificationMessageList = new ArrayList<>();
    private HashSet<Long> chatIdSet = new HashSet<>();

    private NotificationHandler() {

    }

    synchronized public void addNotificationMessage(long chatId, String from, String message, String mimeType, byte[] image) {
        NotificationMessage notificationMessage = new NotificationMessage(chatId, from, message, mimeType, image);
        notificationMessageList.add(notificationMessage);

        chatIdSet.add(chatId);
    }

    public int getNotificationChatCount() {
        return chatIdSet.size();
    }

    public int getNotificationMessagesCount() {
        int messageCount = notificationMessageList.size();
        return messageCount;
    }

    public void showNotification(Context context, PendingIntent pendingIntent, long chatId) {
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

    synchronized public void clearNotificationMessages(long chatId) {
        chatIdSet.remove(chatId);

        for (int index = 0; index < notificationMessageList.size(); index++) {
            if (notificationMessageList.get(index).chatId == chatId) {
                notificationMessageList.remove(index);
                index--;
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

        if (TinyDB.getInstance(context).getBoolean(TinyDB.NOTIFICATION_PREVIEW, SettingsActivity.NOTIFICATION_PREVIEWS_DEFAULT_VALUE)) {
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

        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);

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

        if (TinyDB.getInstance(context).getBoolean(TinyDB.NOTIFICATION_PREVIEW, SettingsActivity.NOTIFICATION_PREVIEWS_DEFAULT_VALUE)) {
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
        int defaults = 0;
        defaults = getDefaults(context, defaults, builder);
        builder.setDefaults(defaults);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationHandler.NOTIFICATION_ID, builder.build());
    }

    private int getDefaults(Context context, int defaults, NotificationCompat.Builder builder) {
        if (vibrationEnabled(context)) {
            defaults = defaults | Notification.DEFAULT_VIBRATE;
        } else {
            builder.setVibrate(new long[]{0l});
        }
        if (soundEnabled(context)) {
            defaults = defaults | Notification.DEFAULT_SOUND;
        } else {
            builder.setSound(null);
        }
        if (lightEnabled(context)) {
            builder.setLights(0xFF0000FF, 100, 3000);
        } else {
//            defaults = defaults | Notification.DEFAULT_LIGHTS;
            //nothing
        }
        return defaults;
    }

    private boolean vibrationEnabled(Context context) {
        if (TinyDB.getInstance(context).getBoolean(TinyDB.VIBRATE, SettingsActivity.VIBRATE_DEFAULT_VALUE)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean soundEnabled(Context context) {
        if (TinyDB.getInstance(context).getBoolean(TinyDB.NOTIFICATION_SOUND, SettingsActivity.NOTIFICATION_SOUND_DEFAULT_VALUE)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean lightEnabled(Context context) {
        if (TinyDB.getInstance(context).getBoolean(TinyDB.LIGHT, SettingsActivity.LIGHT_DEFAULT_VALUE)) {
            return true;
        } else {
            return false;
        }
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

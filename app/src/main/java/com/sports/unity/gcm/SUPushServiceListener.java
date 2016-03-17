package com.sports.unity.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.google.android.gms.gcm.GcmListenerService;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONObject;

/**
 * Created by madmachines on 17/3/16.
 */
public class SUPushServiceListener extends GcmListenerService {

    private static final String TAG = "SUPushServiceListener";
    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        sendNotification(message);

    }

    private void sendNotification(String message) {

        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.tour_icon;
         long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, message, when);
        Intent notificationIntent = new Intent(this, ScoreDetailActivity.class);
        notificationIntent.putExtra(Constants.INTENT_KEY_ID,1);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.push_notification_layout);
        contentView.setImageViewResource(R.id.iv_player, R.drawable.ic_cricket);
         contentView.setTextViewText(R.id.tv_notification_subject, message);
         contentView.setTextViewText(R.id.tv_notification_content, message);
         contentView.setTextViewText(R.id.tv_current_time, DateUtil.getCurrentTime());
         contentView.setImageViewResource(R.id.iv_notification_icon, R.drawable.ic_flash_on);
         notification.contentView = contentView;
         notification.contentIntent = contentIntent;
         mNotificationManager.notify(0, notification);
    }
}

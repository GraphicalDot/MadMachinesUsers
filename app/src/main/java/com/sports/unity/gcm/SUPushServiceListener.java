package com.sports.unity.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import static com.sports.unity.util.Constants.INTENT_KEY_TYPE;

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

        RemoteViews contentView = new RemoteViews(getPackageName(),
                R.layout.push_notification_layout);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_selected_sports)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.app_name));
        Intent i = new Intent(this, ScoreDetailActivity.class);
        i.putExtra(INTENT_KEY_TYPE,Constants.SPORTS_TYPE_CRICKET);
        i.putExtra(Constants.INTENT_KEY_ID,"icc_wc_t20_2016_g19");
        i.putExtra(Constants.INTENT_KEY_MATCH_STATUS, "notstarted");
        i.putExtra(Constants.INTENT_KEY_MATCH_LIVE, false);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ScoreDetailActivity.class);
        stackBuilder.addNextIntent(i);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
           /* mBuilder.setContent(contentView);*/
        contentView.setImageViewResource(R.id.iv_player, R.drawable.ic_cricket);
        contentView.setTextViewText(R.id.tv_notification_subject, message);
        contentView.setTextViewText(R.id.tv_notification_content, message);
        contentView.setTextViewText(R.id.tv_current_time, DateUtil.getCurrentTime());
        contentView.setImageViewResource(R.id.iv_notification_icon, R.drawable.ic_flash_on);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());








    }
}

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
import com.sports.unity.util.SPORTSENUM;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sports.unity.util.Constants.INTENT_KEY_TYPE;

/**
 * Created by madmachines on 17/3/16.
 */
public class SUPushServiceListener extends GcmListenerService {

    private static final String TAG = "SUPushServiceListener";
    private String sportsType;
    private String matchiId;
    private String seriesid;
    private String matchStatus;
    private boolean isLive;
    private String title;
    private String content;
    private String event;

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

        try {
            JSONObject notification = new JSONObject(message);
            if (!notification.isNull(GCMConstants.SPORTS_ID)){
                sportsType = notification.getInt(GCMConstants.SPORTS_ID)==1?Constants.SPORTS_TYPE_CRICKET:Constants.SPORTS_TYPE_FOOTBALL;
            }
            if (!notification.isNull(GCMConstants.MATCH_ID)){
                matchiId = notification.getString(GCMConstants.MATCH_ID);
            }
            if (!notification.isNull(GCMConstants.LEAGUE_SERIES_ID)) {
                seriesid = notification.getString(GCMConstants.LEAGUE_SERIES_ID);
            }
            if (!notification.isNull(GCMConstants.MATCH_STATUS)) {
                matchStatus= notification.getString(GCMConstants.MATCH_STATUS);
            }
            if (!notification.isNull(GCMConstants.TOP_TEXT)) {
                title = notification.getString(GCMConstants.TOP_TEXT);
            }
            if (!notification.isNull(GCMConstants.BOTTOM_TEXT)) {

                content = notification.getString(GCMConstants.BOTTOM_TEXT);
            }
            if (!notification.isNull(GCMConstants.EVENT_ID)) {
                event = notification.getString(GCMConstants.EVENT_ID);
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notification_enable)
                            .setContentTitle(title)
                            .setContentText(content);
            Intent i = new Intent(this, ScoreDetailActivity.class);
            i.putExtra(INTENT_KEY_TYPE,sportsType);
            i.putExtra(Constants.INTENT_KEY_ID,matchiId);
            i.putExtra(Constants.INTENT_KEY_SERIES,seriesid);
            i.putExtra(Constants.INTENT_KEY_MATCH_STATUS,matchStatus);
            //i.putExtra(Constants.INTENT_KEY_MATCH_LIVE, isLive);
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

      } catch (JSONException e) {
            e.printStackTrace();
        }








    }
}

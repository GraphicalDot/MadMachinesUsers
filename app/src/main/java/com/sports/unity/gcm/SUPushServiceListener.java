package com.sports.unity.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

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
    private int event;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "Data: " + data);


        String message = data.getString("data");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        sendNotification(message);
    }


    private void sendNotification(String message) {
       try {

            if(message!=null) {
                JSONObject oldData = new JSONObject(message);
                JSONObject notification = oldData.getJSONObject("message");
                if (!notification.isNull(GCMConstants.SPORTS_ID)) {
                    int sportsId = notification.getInt(GCMConstants.SPORTS_ID);
                    if(sportsId==1 || sportsId==2){
                        sportsType = sportsId == 1 ? Constants.SPORTS_TYPE_CRICKET : Constants.SPORTS_TYPE_FOOTBALL;
                    }
                }
                if (!notification.isNull(GCMConstants.MATCH_ID)) {
                    matchiId = notification.getString(GCMConstants.MATCH_ID);
                }
                if (!notification.isNull(GCMConstants.LEAGUE_SERIES_ID)) {
                    seriesid = notification.getString(GCMConstants.LEAGUE_SERIES_ID);
                }
                if (!notification.isNull(GCMConstants.MATCH_STATUS)) {
                    matchStatus = notification.getString(GCMConstants.MATCH_STATUS);
                }
                if (!notification.isNull(GCMConstants.TOP_TEXT)) {
                    title = notification.getString(GCMConstants.TOP_TEXT);
                }
                if (!notification.isNull(GCMConstants.BOTTOM_TEXT)) {

                    content = notification.getString(GCMConstants.BOTTOM_TEXT);
                }
                if (!notification.isNull(GCMConstants.EVENT_ID)) {
                    event = notification.getInt(GCMConstants.EVENT_ID);
                }
                Intent i =null;

                int  drawableId = 0;


                if(sportsType!=null){
                    if(Constants.SPORTS_TYPE_CRICKET.equalsIgnoreCase(sportsType)){

                        drawableId= getDrawableIconCricket(event);
                        if(matchiId!=null && seriesid!=null && matchStatus!=null && title!=null && content!=null && event!=0  ){
                            i = new Intent(this, ScoreDetailActivity.class);
                            i.putExtra(INTENT_KEY_TYPE, sportsType);
                            i.putExtra(Constants.INTENT_KEY_ID, matchiId);
                            i.putExtra(Constants.INTENT_KEY_SERIES, seriesid);
                            i.putExtra(Constants.INTENT_KEY_MATCH_STATUS, matchStatus);
                        }else{
                            i = new Intent(this, MainActivity.class);
                        }




                    }else if(Constants.SPORTS_TYPE_FOOTBALL.equalsIgnoreCase(sportsType)){
                        drawableId=  getDrawableIconFootball(event);
                        if(matchiId!=null && seriesid!=null && matchStatus!=null && title!=null && content!=null && event!=0  ){
                            i = new Intent(this, ScoreDetailActivity.class);
                            i.putExtra(INTENT_KEY_TYPE, sportsType);
                            i.putExtra(Constants.INTENT_KEY_ID, matchiId);
                            i.putExtra(Constants.LEAGUE_NAME, seriesid);
                            i.putExtra(Constants.INTENT_KEY_MATCH_STATUS, matchStatus);
                            i.putExtra(Constants.INTENT_KEY_MATCH_LIVE, matchStatus.equalsIgnoreCase("L")?true:false);
                        }else{
                            i = new Intent(this, MainActivity.class);
                        }


                    }else{
                        drawableId = 0;

                    }


                }

               Intent muteIntent = new Intent(this,UnRegisterMatch.class);
                muteIntent.putExtra(Constants.INTENT_KEY_ID,matchiId);
                muteIntent.putExtra(Constants.INTENT_KEY_SERIES,seriesid);
                PendingIntent mpi = PendingIntent.getService(this,0,muteIntent,PendingIntent.FLAG_UPDATE_CURRENT) ;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title + " " + content);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, title+" "+content);

                PendingIntent shareIntent = PendingIntent.getActivity(this,0,sharingIntent,PendingIntent.FLAG_UPDATE_CURRENT);


                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), drawableId);
                //int sportsTypeId = getSportId(sportsType);
                 NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setSmallIcon(R.drawable.ic_stat_notification)
                                .setLargeIcon(largeIcon)
                                .setContentTitle(title)
                                .setContentText(content)
                                .setColor(getResources().getColor(R.color.app_theme_blue))
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .addAction(R.drawable.ic_share_white,getString(R.string.share), shareIntent)
                                .addAction(R.drawable.ic_mute_notification, getString(R.string.mute_alerts), mpi);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(ScoreDetailActivity.class);
                stackBuilder.addNextIntent(i);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                //mBuilder.setContent(contentView);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
            }
      } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private int getDrawableIconFootball(int event) {
        int  drawable=0;
        switch (event) {
            case 1:
                drawable = R.drawable.ic_postpond_notification;
                break;
            case 2:
                drawable = R.drawable.ic_lineups_confirmed_notification;
                break;
            case 3:
                drawable = R.drawable.ic_match_started;
                break;
            case 4:
                drawable = R.drawable.ic_red_card_notification;
                break;
            case 5:
                drawable = R.drawable.ic_yellow_card_notification;
                break;
            case 6:
                drawable = R.drawable.ic_full_time_notification;
                break;
            case 7:
                drawable = R.drawable.ic_goal_notification;
                break;
            case 8:
                drawable = R.drawable.ic_goal_notification;
                break;
            case 9:
                drawable = R.drawable.ic_goal_notification;
                break;
            case 10:
                drawable = R.drawable.ic_penalty_missed_notification;
                break;
            case 11:
                drawable = R.drawable.ic_penalty_missed_notification;
                break;
        }
        return  drawable;
    }


    private int getDrawableIconCricket(int event) {
        int  drawable=0;
        switch (event){
            case 1:
                drawable = R.drawable.ic_toss;
                break;
            case 2:
                drawable = R.drawable.ic_match_started;
                break;
            case 3:
                drawable = R.drawable.ic_wkt;
                break;
            case 4:
                drawable = R.drawable.ic_four;
                break;
            case 5:
                drawable = R.drawable.ic_six;
                break;
            case 6:
                drawable = R.drawable.ic_half_century;
                break;
            case 7:
                drawable = R.drawable.ic_century;
                break;
            case 8:
                drawable = R.drawable.ic_no_img;
                break;
            case 9:
                drawable = R.drawable.ic_win_no_flag_available;
                break;
            case 10:
                drawable = R.drawable.ic_no_img;
                break;
            case 11:
                drawable = R.drawable.ic_cricket_group;
                break;
            case 12:
                drawable = R.drawable.ic_mute_notification;
                break;
            case 13:
                drawable = R.drawable.ic_no_img;
                break;
        }
      return drawable;
    }

    private int getSportId(String sports) {
        int  drawable=0;
        switch (sports){
            case Constants.SPORTS_TYPE_CRICKET :
                drawable = R.drawable.ic_cricket;
                break;
            case Constants.SPORTS_TYPE_FOOTBALL:
                drawable = R.drawable.ic_football;
                break;

        }
        return drawable;
    }

}

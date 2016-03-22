package com.sports.unity.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sports.unity.BuildConfig;
import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.sports.unity.util.Constants.INTENT_KEY_TYPE;

/**
 * Created by madmachines on 17/3/16.
 */
public class RegistrationIntentService extends IntentService implements TokenRegistrationHandler.TokenRegistrationContentListener
{
    private TokenRegistrationHandler tokenRegistrationHandler;
    private SharedPreferences preferences;
    String userName = "b";
    String password = "password";
    private NotificationManager mNotificationManager;

    public RegistrationIntentService()
    {
        super("RegistrationIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try
        {
            String token = getToken();
            Log.i("Token", "onHandleIntent: " + token);
            if(preferences!=null){
                registerToken(token);
            }

        }
        catch (IOException e)
        {
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }



    private String getToken() throws IOException {
        InstanceID instanceID = InstanceID.getInstance(this);
        return instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
    }

    /**
     * @param token
     * @return
     */
    public void registerToken(String token)
    {

        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.registrerToken(token);
    }


    public void removeToken(String uuid)
    {

        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.removeToken();
    }
   @Override
    public void handleContent(String content) {
        try {
            JSONObject object = new JSONObject(content);
            if(object!=null){
                if(200==object.getInt("status")){
                    if("success".equalsIgnoreCase(object.getString("info"))) {
                        preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
                        sendNotification("Notification");

                    }else{
                        preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
                    }
                }else {
                    preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
                }
            }else{
                preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
            }



        } catch (JSONException e) {
            e.printStackTrace();
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
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
        i.putExtra(INTENT_KEY_TYPE, Constants.SPORTS_TYPE_CRICKET);
        i.putExtra(Constants.INTENT_KEY_ID, "icc_wc_t20_2016_g19");
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
           // mBuilder.setContent(contentView);
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

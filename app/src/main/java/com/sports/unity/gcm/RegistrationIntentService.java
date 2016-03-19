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
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
            String uuid = getUUID();
            Log.i("Token", "onHandleIntent: " + token);
            if(preferences!=null){
                registerToken(token,uuid);
            }

        }
        catch (IOException e)
        {
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private String getUUID() {
        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }

    private String getToken() throws IOException {
        InstanceID instanceID = InstanceID.getInstance(this);
        return instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
    }

    /**
     * @param token
     * @return
     */
    public void registerToken(String token,String uuid)
    {

        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.registrerToken(userName, password, token, uuid);
    }


    public void removeToken(String uuid)
    {

        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.removeToken(userName, password, uuid);
    }



    /*@Override
    public void onDestroy() {
        super.onDestroy();
        try
        {

            String uuid = getUUID();
            Log.i("Token", "onHandleIntent: " + uuid);
            if(preferences!=null){
                boolean sentToken= preferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER,false);
                if(sentToken){
                    removeToken(uuid);
                }
            }

        }
        catch (Exception e)
        {
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }
*/
    @Override
    public void handleContent(String content) {
        try {
            JSONObject object = new JSONObject(content);
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


        } catch (JSONException e) {
            e.printStackTrace();
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void sendNotification(String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_blank_img)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ScoreDetailActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ScoreDetailActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());






    }
}

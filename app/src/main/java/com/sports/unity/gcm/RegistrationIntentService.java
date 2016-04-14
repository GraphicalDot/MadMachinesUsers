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

import org.json.JSONArray;
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
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i("Token", "onHandleIntent: " + token);
            if(preferences!=null){
                registerToken(token);
                SharedPreferences.Editor editor= preferences.edit();
                editor.putString(Constants.REQUEST_PARAMETER_KEY_TOKEN, token);
                editor.apply();

            }
        }
        catch (IOException e)
        {
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
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
            if(object!=null && !object.isNull("status") && 200==object.getInt("status") && "success".equalsIgnoreCase(object.getString("info"))){                SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Constants.SENT_TOKEN_TO_SERVER, true);
                    if (!object.isNull("match_ids")) {
                        JSONArray matchIds = object.getJSONArray("match_ids");
                        int l = matchIds.length();
                        for (int i = 0; i < l; i++) {
                            String matchSeriesId = matchIds.getString(i);
                            editor.putString(matchSeriesId, matchSeriesId);
                        }
                    }
                    editor.apply();
            }else {
                preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }


}

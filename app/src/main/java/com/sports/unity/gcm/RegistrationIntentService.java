package com.sports.unity.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.scoredetails.cricketdetail.CompletedMatchScoreCardHandler;
import com.sports.unity.util.Constants;

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
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            registerToken(token);
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
        String userName = null;
        String password = null;
        String udid = null;
        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.registrerToken(userName,password,token,udid);
    }


    @Override
    public void handleContent(String content) {
        try {
            JSONObject object = new JSONObject(content);
             if(object.getBoolean("success")){
                 preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
             }else{
                 preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
             }
        } catch (JSONException e) {
            e.printStackTrace();
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }
}

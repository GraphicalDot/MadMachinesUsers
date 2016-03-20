package com.sports.unity.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

/*import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.iid.InstanceID;
import com.sports.unity.util.Constants;

import java.io.IOException;

import static com.apptentive.android.sdk.Apptentive.PUSH_PROVIDER_APPTENTIVE;
import static com.apptentive.android.sdk.Apptentive.setPushNotificationIntegration;

import static com.google.android.gms.gcm.GoogleCloudMessaging.INSTANCE_ID_SCOPE;*/


public class RegistrationIntentService extends IntentService
{

    private String[] TOPICS = {"su.cricket", "su.football"};
    private int gcm_defaultSenderId;

    public RegistrationIntentService()
    {
        super("RegistrationIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent)
   {
       /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try
        {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token =
                    instanceID.getToken(getString(gcm_defaultSenderId), INSTANCE_ID_SCOPE, null);
            subscribeTopics(token);
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
            registerToken(token);
        }
        catch (IOException e)
        {
            preferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }*/
    }

    private void registerToken(String token)
    {
       // setPushNotificationIntegration(getApplicationContext(), PUSH_PROVIDER_APPTENTIVE, token);
    }

    private void subscribeTopics(String token) throws IOException
    {
        /*GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS)
        {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }*/
    }

}

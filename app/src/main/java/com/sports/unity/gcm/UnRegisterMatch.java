package com.sports.unity.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.sports.unity.util.Constants;

import org.json.JSONObject;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class UnRegisterMatch extends IntentService implements TokenRegistrationHandler.TokenRegistrationContentListener{


    private TokenRegistrationHandler tokenRegistrationHandler;
    private String matchId;

    public UnRegisterMatch() {
        super("UnRegisterMatch");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String matchId = intent.getStringExtra(Constants.INTENT_KEY_ID);
            String seriesId = intent.getStringExtra(Constants.INTENT_KEY_SERIES);
            removeMatchUser(matchId+"|"+seriesId);
        }
    }

    public void removeMatchUser(String matchId)
    {
        this.matchId = matchId;
        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.removeMatchUser(matchId);
    }

    @Override
    public void handleContent(String content) {
        try{
            Log.i("Remove Match", "handleContent: "+content);
            JSONObject jsonObject = new JSONObject(content);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if(jsonObject!=null && 200 == jsonObject.getInt("status") && jsonObject.getString("info").equalsIgnoreCase("success")){
                SharedPreferences.Editor editor = preferences.edit();
                if(TextUtils.isEmpty(preferences.getString(matchId, "")))
                {
                    editor.remove(matchId);

                }
                editor.apply();
            }
        }catch (Exception e){e.printStackTrace();}


    }
}

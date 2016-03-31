package com.sports.unity.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.sports.unity.util.Constants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class UnRegisterMatch extends IntentService implements TokenRegistrationHandler.TokenRegistrationContentListener{


    private TokenRegistrationHandler tokenRegistrationHandler;

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

    public void removeMatchUser(String token)
    {

        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.removeMatchUser(token);
    }

    @Override
    public void handleContent(String content) {

    }
}

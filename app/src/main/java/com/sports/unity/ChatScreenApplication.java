package com.sports.unity;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;

/**
 * Created by madmachines on 17/9/15.
 */
public class ChatScreenApplication extends Application {

    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        /*if ( BuildConfig.DEBUG ) {
            GoogleAnalytics.getInstance(this).setDryRun(true);
        }
        getDefaultTracker();*/
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    /*synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            mTracker.enableExceptionReporting(true);

            if(UserUtil.isProfileCreated()) {
                TinyDB tinyDB = TinyDB.getInstance(this);
                mTracker.set("&uid", tinyDB.getString(TinyDB.KEY_USER_JID));
            }
        }
        return mTracker;
    }*/

    /*public void userLoginTrack(){
        TinyDB tinyDB = TinyDB.getInstance(this);
        getDefaultTracker().set("&uid", tinyDB.getString(TinyDB.KEY_USER_JID));

        getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("User Sign In")
                .build());
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

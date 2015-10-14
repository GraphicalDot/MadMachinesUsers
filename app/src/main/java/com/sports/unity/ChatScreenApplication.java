package com.sports.unity;

import android.app.Application;

/**
 * Created by madmachines on 17/9/15.
 */
public class ChatScreenApplication extends Application {

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static void activityDestroyed() {
        activityVisible = false;
    }

    public static void activityStopped() {
        activityVisible = false;
    }


    private static boolean activityVisible;
}

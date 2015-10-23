package com.sports.unity.common.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by madmachines on 21/10/15.
 */
public class UserUtil {

    private static boolean USER_REGISTERED = false;
    private static boolean PROFILE_CREATED = false;
    private static ArrayList<String> SPORTS_SELECTED = null;

    public static void init(Context context){
        TinyDB tinyDB = TinyDB.getInstance(context);
        USER_REGISTERED = tinyDB.getBoolean( TinyDB.KEY_REGISTERED, false);
        PROFILE_CREATED = tinyDB.getBoolean(TinyDB.KEY_PROFILE_CREATED, false);

        SPORTS_SELECTED = tinyDB.getListString( TinyDB.KEY_SPORTS_SELECTED);
    }

    public static void setUserRegistered(Context context, boolean userRegistered) {
        USER_REGISTERED = userRegistered;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean( TinyDB.KEY_REGISTERED, userRegistered);
    }

    public static void setProfileCreated(Context context, boolean profileCreated) {
        PROFILE_CREATED = profileCreated;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.KEY_PROFILE_CREATED, profileCreated);
    }

    public static void setSportsSelected(Context context, ArrayList<String> sportsSelected) {
        SPORTS_SELECTED = sportsSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putListString( TinyDB.KEY_SPORTS_SELECTED, sportsSelected);
    }

    public static boolean isUserRegistered() {
        return USER_REGISTERED;
    }

    public static boolean isProfileCreated() {
        return PROFILE_CREATED;
    }

    public static boolean isSportsSelected() {
        return ! SPORTS_SELECTED.isEmpty();
    }

    public static ArrayList<String> getSportsSelected() {
        return SPORTS_SELECTED;
    }

}

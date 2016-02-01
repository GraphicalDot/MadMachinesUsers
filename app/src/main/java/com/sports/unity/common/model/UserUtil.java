package com.sports.unity.common.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by madmachines on 21/10/15.
 */
public class UserUtil {

    private static boolean USER_REGISTERED = false;
    private static boolean OTP_SENT = false;
    private static boolean PROFILE_CREATED = false;
    private static ArrayList<String> SPORTS_SELECTED = null;


    private static boolean leagueSelected;
    private static boolean teamSelected;
    private static boolean playerSelected;
    private static boolean filterCompleted;
    private static ArrayList<String> favFilterList = null;

    public static void init(Context context) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        USER_REGISTERED = tinyDB.getBoolean(TinyDB.KEY_REGISTERED, false);
        PROFILE_CREATED = tinyDB.getBoolean(TinyDB.KEY_PROFILE_CREATED, false);
        OTP_SENT = tinyDB.getBoolean(TinyDB.KEY_OTP_SENT, false);

        SPORTS_SELECTED = tinyDB.getListString(TinyDB.KEY_SPORTS_SELECTED);

        favFilterList = tinyDB.getListString(TinyDB.FAVOURITE_FILTERS);

        leagueSelected =tinyDB.getBoolean(TinyDB.LEAGUE_SELECTION, false);
        teamSelected =tinyDB.getBoolean(TinyDB.TEAM_SELECTION, false);
        playerSelected =tinyDB.getBoolean(TinyDB.PLAYER_SELECTION, false);
        filterCompleted=tinyDB.getBoolean(TinyDB.FILTER_COMPLETE, false);
    }

    public static void setUserRegistered(Context context, boolean userRegistered) {
        USER_REGISTERED = userRegistered;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.KEY_REGISTERED, userRegistered);
    }

    public static void setProfileCreated(Context context, boolean profileCreated) {
        PROFILE_CREATED = profileCreated;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.KEY_PROFILE_CREATED, profileCreated);
    }

    public static void setSportsSelected(Context context, ArrayList<String> sportsSelected) {
        SPORTS_SELECTED = sportsSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putListString(TinyDB.KEY_SPORTS_SELECTED, sportsSelected);
    }

    public static void setOtpSent(Context context, boolean otpSent) {
        OTP_SENT = otpSent;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.KEY_OTP_SENT, otpSent);
    }

    public static boolean isUserRegistered() {
        return USER_REGISTERED;
    }

    public static boolean isOtpSent() {
        return OTP_SENT;
    }

    public static boolean isProfileCreated() {
        return PROFILE_CREATED;
    }

    public static boolean isSportsSelected() {
        return !SPORTS_SELECTED.isEmpty();
    }

    public static ArrayList<String> getSportsSelected() {
        return SPORTS_SELECTED;
    }

    public static void setLeagueSelected(Context context, boolean isLeagueSelected) {
        leagueSelected = isLeagueSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.LEAGUE_SELECTION, isLeagueSelected);
    }
    public static boolean isLeagueSelected(){
        return leagueSelected;
    }

    public static void setTeamSelected(Context context, boolean isTeamSelected) {
        teamSelected = isTeamSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.TEAM_SELECTION, isTeamSelected);
    }
    public static boolean isTeamSelected(){
        return teamSelected;
    }

    public static void setPlayerSelected(Context context, boolean isPlayerSelected) {
        playerSelected = isPlayerSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.PLAYER_SELECTION, isPlayerSelected);
    }
    public static boolean isPlayerSelected(){
        return playerSelected;
    }
    public static void setFilterCompleted(Context context, boolean isFilterCompleted) {
        filterCompleted = isFilterCompleted;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.FILTER_COMPLETE, isFilterCompleted);
    }
    public static boolean isFilterCompleted(){
        return filterCompleted;
    }
    public static void setFavouriteFilters(Context context, ArrayList<String> footballFilterTeam) {
        favFilterList = new ArrayList<String>(footballFilterTeam);

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putListString(TinyDB.FAVOURITE_FILTERS, favFilterList);
    }

    public static ArrayList<String> getFavouriteFilters() {
        return favFilterList;
    }

}

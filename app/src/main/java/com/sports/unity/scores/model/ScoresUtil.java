package com.sports.unity.scores.model;

/**
 * Created by amandeep on 29/4/16.
 */
public class ScoresUtil {

    public static boolean isCricketMatchLive(String matchStatus){
        boolean live = false;
        if( matchStatus.equalsIgnoreCase("L") ){
            live = true;
        }
        return live;
    }

    public static boolean isCricketMatchUpcoming(String matchStatus){
        boolean upcoming = false;
        if( matchStatus.equalsIgnoreCase("N") || matchStatus.trim().equalsIgnoreCase("") ){
            upcoming = true;
        }
        return upcoming;
    }

    public static boolean isCricketMatchCompleted(String matchStatus){
        boolean completed = false;
        if( isCricketMatchLive(matchStatus) == false && isCricketMatchUpcoming(matchStatus) == false ){
            completed = true;
        }
        return completed;
    }

}

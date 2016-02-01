package com.sports.unity.common.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mad on 1/7/2016.
 */
public final class FavouriteJsonParser {
    public static ArrayList<JSONObject> parseFavouriteList(String jsonContent) {

        ArrayList<JSONObject> objList = new ArrayList<JSONObject>();
        try {
            JSONObject jsonObject=new JSONObject(jsonContent);
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            for( int index=0; index < jsonArray.length(); index++){
                objList.add(jsonArray.getJSONObject(index));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  objList;
    }
    public static ArrayList<JSONObject> parseFavouriteFootballTeam() {

        ArrayList<JSONObject> footballTeam = new ArrayList<JSONObject>();


        return  footballTeam;
    }
    public static ArrayList<JSONObject> parseFavouriteFootballPlayer() {

        ArrayList<JSONObject> footabllPlayer = new ArrayList<JSONObject>();


        return  footabllPlayer;
    }

    public static ArrayList<JSONObject> parseFavouriteCricketTeam() {

        ArrayList<JSONObject> cricketTeam = new ArrayList<JSONObject>();


        return  cricketTeam;
    }
    public static ArrayList<JSONObject> parseFavouriteCricketPlayer() {

        ArrayList<JSONObject> cricketPlayer = new ArrayList<JSONObject>();


        return  cricketPlayer;
    }
}

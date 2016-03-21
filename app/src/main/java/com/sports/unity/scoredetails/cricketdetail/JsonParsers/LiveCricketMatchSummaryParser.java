package com.sports.unity.scoredetails.cricketdetail.JsonParsers;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 21/3/16.
 */
public class LiveCricketMatchSummaryParser   extends JsonObjectCaller {
    private JSONObject cricketSummary;
    private JSONObject currentBowler;
    public JSONObject getMatchSummary() throws JSONException {
        return jsonObject.getJSONObject("summary");
    }
    public void setCricketSummary(JSONObject cricketSummary) {
        this.cricketSummary = cricketSummary;
    }

    public JSONObject getCurentBowler() throws JSONException {

        return cricketSummary.getJSONObject("current_bowler");
    }
    public JSONObject getCurrentPartnership() throws JSONException {
        return cricketSummary.getJSONObject("current_partnership");
    }


    public JSONArray getRecentOver() throws JSONException {
        return cricketSummary.getJSONArray("recent_over");
    }

    public JSONObject getUmpires() throws JSONException {
        return cricketSummary.getJSONObject("umpires");
    }
    public JSONArray getUpCommingBatsMan() throws JSONException {
        return cricketSummary.getJSONArray("upcoming_batsmen");
    }

    public void setCurrentBowler(JSONObject currentBowler){
        this.currentBowler = currentBowler;
    }

    public String getCurentBowlerName() throws JSONException {
        if(currentBowler.isNull("name")){
            return  "";
        }
        return currentBowler.getString("name");
    }
    public String getCurentBowlerOvers() throws JSONException {
        if(currentBowler.isNull("overs")){
            return  "";
        }
        return currentBowler.getString("overs");
    }
    public String getCurentBowlerRuns() throws JSONException {
        if(currentBowler.isNull("runs")){
            return  "";
        }
        return currentBowler.getString("runs");
    }


    public String getCurentBowlerWicket() throws JSONException {
        if(currentBowler.isNull("wicket")){
            return  "";
        }
        return currentBowler.getString("wicket");
    }




}

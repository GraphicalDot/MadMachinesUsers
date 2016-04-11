package com.sports.unity.scoredetails.cricketdetail.JsonParsers;

import com.sports.unity.common.view.DonutProgress;
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
    private JSONObject currentPartnership;
    private  JSONArray yetToBat;
    private  JSONObject recentOver;

    public JSONObject getMatchSummary() throws JSONException {
        return jsonObject.getJSONObject("summary");
    }
    public void setCricketSummary(JSONObject cricketSummary) {
        this.cricketSummary = cricketSummary;
    }

    public void setCurrentPartnership(JSONObject currentPartnership) {
        this.currentPartnership = currentPartnership;
    }
    public void setYetToBat(JSONArray yetToBat) {
        this.yetToBat = yetToBat;
    }
    public JSONObject getCurentBowler() throws JSONException {
        if(cricketSummary.isNull("current_bowler")){
            return  new JSONObject();
        }
        return cricketSummary.getJSONObject("current_bowler");
    }
    public JSONArray getCurrentPartnership() throws JSONException {
        return cricketSummary.getJSONArray("current_partnership");
    }


    public JSONObject getRecentOver() throws JSONException {
        return cricketSummary.getJSONObject("recent_over");
    }

    public void setRecentOver(JSONObject recentOver) throws JSONException {
        this.recentOver = recentOver;
    }

    public JSONObject getUmpires() throws JSONException {
        return cricketSummary.getJSONObject("umpires");
    }
    public JSONArray getUpCommingBatsMan() throws JSONException {

        if(cricketSummary.isNull("upcoming_batsmen")){
            return new JSONArray();
        }
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

    public String getCurentBowlerImage() throws JSONException {
        if(currentBowler.isNull("player_image")){
            return  "";
        }
        return currentBowler.getString("player_image");
    }
    public String getPlayeFirstName() throws  JSONException{
        if(!currentPartnership.isNull("player_1")){
            return  currentPartnership.getString("player_1");
        }else  {return  "";}
    }

    public int getPlayeFirstBalls() throws  JSONException{
        if(!currentPartnership.isNull("player_1_balls")){
            return  currentPartnership.getInt("player_1_balls");
        }else  {return 0;}
    }
    public Integer getPlayeFirstRuns() throws  JSONException{
        if(!currentPartnership.isNull("player_1_runs")){
            return  currentPartnership.getInt("player_1_runs");
        }else  {return  0;}
    }

    public String getPlayerFirstImage() throws JSONException {
        if(currentPartnership.isNull("player_1_image")){
            return  "";
        }
        return currentPartnership.getString("player_1_image");
    }

    public String getPlayeSecondName() throws  JSONException{
        if(!currentPartnership.isNull("player_2")){
            return  currentPartnership.getString("player_2");
        }else  {return  "";}
    }

    public int getPlayeSecondBalls() throws  JSONException{
        if(!currentPartnership.isNull("player_2_balls")){
            return  currentPartnership.getInt("player_2_balls");
        }else  {return  0;}
    }
    public Integer getPlayeSecondRuns() throws  JSONException{
        if(!currentPartnership.isNull("player_2_runs")){
            return  currentPartnership.getInt("player_2_runs");
        }else  {return  0;}
    }

    public String getPlayerSecondImage() throws JSONException {
        if(currentPartnership.isNull("player_2_image")){
            return  "";
        }
        return currentPartnership.getString("player_2_image");
    }

    public String getYetToPlayerName(int index) throws JSONException{
           if(yetToBat.get(index)!=null){
               JSONObject nameObject = yetToBat.getJSONObject(index);
               return  nameObject.getString("name");
           }else {return  "";}
    }

    public String getYetToPlayerImage(int index) throws JSONException{
        if(yetToBat.get(index)!=null){
            JSONObject nameObject = yetToBat.getJSONObject(index);
            return  nameObject.getString("player_image");
        }else {return  "";}
    }



    /*public String getRecentOver(int index) throws JSONException{
        if(recentOver.get(index)!=null){
            JSONObject nameObject = recentOver.getJSONObject(index);
            return  nameObject.getString("over");
        }else {return  "";}
    }
    public String getRecentRuns(int index) throws JSONException{
        if(recentOver.get(index)!=null){
            JSONObject nameObject = recentOver.getJSONObject(index);
            return  nameObject.getString("runs");
        }else {return  "";}
    }
    public Boolean getRecentWicket(int index) throws JSONException{
        if(recentOver.get(index)!=null){
            JSONObject nameObject = recentOver.getJSONObject(index);
            return  nameObject.getBoolean("wicket");
        }else {return  false;}
    }*/
}

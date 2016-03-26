package com.sports.unity.scoredetails.cricketdetail.JsonParsers;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 21/3/16.
 */
public class CricketMatchScoreJsonParser  extends JsonObjectCaller{
    private  JSONObject battingObject;
    private  JSONObject bowllingObject;
    private  JSONObject fallofWicketObject;


    public String getAwayTeam() throws JSONException {
        if(jsonObject.isNull("away_team")){
            return  "";
        }
        return jsonObject.getString("away_team");
    }

    public String getHomeTeam() throws JSONException {
        if(jsonObject.isNull("home_team")){
            return  "";
        }
        return jsonObject.getString("home_team");
    }

    public JSONObject getScoreCrad() throws JSONException {

        return jsonObject.getJSONObject("scorecard");
    }

    public JSONObject getTeamFirst(JSONObject scoreCard)throws JSONException{
        return  scoreCard.getJSONObject("1");
    }
    public JSONObject getTeamSecond(JSONObject scoreCard)throws JSONException{
        if(scoreCard.isNull("2")){
            return null;
        }
        return  scoreCard.getJSONObject("2");
    }

    public JSONObject getTeamFirstInnings(final JSONObject teamFirstinnings,final String key)throws JSONException{
        return  teamFirstinnings.getJSONObject(key);
    }
    public JSONObject getTeamSecondInnings(final JSONObject teamSecondinnings,final String key)throws JSONException{
        return  teamSecondinnings.getJSONObject(key);
    }
    public JSONArray getTeamBatting(JSONObject innings )throws JSONException{
        if(innings==null || innings.isNull("batting")){
            return  null;
        }
        return  innings.getJSONArray("batting");
    }
    public JSONArray getTeamBowlling(JSONObject innings )throws JSONException{
        if(innings==null || innings.isNull("bowling")){
            return  null;
        }
        return  innings.getJSONArray("bowling");
    }
    public JSONArray getTeamFallOfWickets(JSONObject innings )throws JSONException{
        if(innings==null || innings.isNull("fall_of_wickets")){
            return  null;
        }
        return  innings.getJSONArray("fall_of_wickets");
    }
    public String getOvers(JSONObject jsonObject) throws  JSONException{
        if(jsonObject!=null && !jsonObject.isNull("overs"))
            return  jsonObject.getString("overs");
        else
            return  "";
    }
    public String getExtra(JSONObject jsonObject) throws  JSONException{
        if(jsonObject!=null && !jsonObject.isNull("extra"))
            return  jsonObject.getString("extra");
        else
            return  "";
    }

    public String getTeamRuns(JSONObject jsonObject) throws  JSONException{
        if(jsonObject!=null && !jsonObject.isNull("runs"))
            return  jsonObject.getString("runs");
        else
            return  "";
    }

    public String getTeamRunsRate(JSONObject jsonObject) throws  JSONException{
        if(jsonObject!=null && !jsonObject.isNull("run_rate"))
            return  jsonObject.getString("run_rate");
        else
            return  "";
    }

    public String getTeamWicket(JSONObject jsonObject) throws  JSONException{
        if(jsonObject!=null && !jsonObject.isNull("wickets"))
            return  jsonObject.getString("wickets");
        else
            return  "";
    }


    public void setBattingObject(JSONObject battingObject){
        this.battingObject = battingObject;
    }
    public void setBowllingObject(JSONObject bowllingObject){
        this.bowllingObject = bowllingObject;
    }
    public void setFallofWicketObject(JSONObject fallofWicketObject){
        this.fallofWicketObject = fallofWicketObject;
    }

    public String getBall() throws  JSONException{
        if(!battingObject.isNull("balls"))
            return  battingObject.getString("balls");
        else
            return  "";
    }

    public String getBatsManName() throws  JSONException{
        if(!battingObject.isNull("batsman_name"))
            return  battingObject.getString("batsman_name");
        else
            return  "";
    }

    public String getBatsManFours() throws  JSONException{
        if(!battingObject.isNull("four"))
            return  battingObject.getString("four");
        else
            return  "";
    }
    public String getBatsManRun() throws  JSONException{
        if(!battingObject.isNull("runs"))
            return  battingObject.getString("runs");
        else
            return  "";
    }

    public String getBatsManSix() throws  JSONException{
        if(!battingObject.isNull("six"))
            return  battingObject.getString("six");
        else
            return  "";
    }
    public String getBatsManStrikeRate() throws  JSONException{
        if(!battingObject.isNull("strike_rate"))
            return  battingObject.getString("strike_rate");
        else
            return  "";
    }
    public String getBatsmanStatus() throws  JSONException{
        if(!battingObject.isNull("how_out"))
            return  battingObject.getString("how_out");
        else
            return  "";
    }

    public String getBowlerRuns() throws  JSONException{
        if(!bowllingObject.isNull("runs"))
            return  bowllingObject.getString("runs");
        else
            return  "";
    }
    public String getBowlerName() throws  JSONException{
        if(!bowllingObject.isNull("bowler_name"))
            return  bowllingObject.getString("bowler_name");
        else
            return  "";
    }

    public String getBowlerEconomy() throws  JSONException{
        if(!bowllingObject.isNull("economy"))
            return  bowllingObject.getString("economy");
        else
            return  "";
    }

    public String getBowlerMaidenOvers() throws  JSONException{
        if(!bowllingObject.isNull("maidens"))
            return  bowllingObject.getString("maidens");
        else
            return  "";
    }
    public String getBowlerOvers() throws  JSONException{
        if(!bowllingObject.isNull("overs"))
            return  bowllingObject.getString("overs");
        else
            return  "";
    }
    public String getBowlerWicket() throws  JSONException{
        if(!bowllingObject.isNull("wickets"))
            return  bowllingObject.getString("wickets");
        else
            return  "";
    }
    public String getBowlerExtra() throws  JSONException{
        if(!bowllingObject.isNull("extra"))
            return  bowllingObject.getString("extra");
        else
            return  "";
    }

    public String getFallOfWicketOver() throws  JSONException{
        if(!fallofWicketObject.isNull("fow_over"))
            return  fallofWicketObject.getString("fow_over");
        else
            return  "";
    }


    public String getFallOfWicketScore() throws  JSONException{
        if(!fallofWicketObject.isNull("fow_score"))
            return  fallofWicketObject.getString("fow_score");
        else
            return  "";
    }
    public String getFallOfName() throws  JSONException{
        if(!fallofWicketObject.isNull("name"))
            return  fallofWicketObject.getString("name");
        else
            return  "";
    }

    public String getFallOfRuns() throws  JSONException{
        if(!fallofWicketObject.isNull("runs"))
            return  fallofWicketObject.getString("runs");
        else
            return  "";
    }
}

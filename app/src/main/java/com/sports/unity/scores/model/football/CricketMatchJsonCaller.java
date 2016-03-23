package com.sports.unity.scores.model.football;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 8/10/15.
 */
public class CricketMatchJsonCaller extends MatchJsonCaller {
     private JSONObject matchWidgetHomeTeam;
     private JSONObject matchWidgetAwayTeam;


    public int getMatchDateTimeEpoch() throws JSONException {
        return jsonObject.getInt("match_time");
    }

    public String getStatus() throws JSONException {
        return jsonObject.getString("status");
    }

    public String getTeam1() throws JSONException {
        return jsonObject.getString("home_team");
    }

    public String getTeam2() throws JSONException {
        return jsonObject.getString("away_team");
    }

    public String getMatchId() throws JSONException {
        return jsonObject.getString("match_id");
    }
    public String getSeriesName() throws JSONException {
        if(jsonObject.isNull("series_name")){
            return "";
        }
        return jsonObject.getString("series_name");
    }
    public String getSeriesId() throws JSONException {
        if(jsonObject.isNull("series_id")){
            return "";
        }
        return jsonObject.getString("series_id");
    }
    public JSONArray getTeamsWiget() throws JSONException {

        return jsonObject.getJSONArray("match_widget");
    }


    public void setMatchWidgetHomeTeam(JSONObject matchWidgetHomeTeam) {
        this.matchWidgetHomeTeam = matchWidgetHomeTeam;
    }

    public void setMatchWidgetAwayTeam(JSONObject matchWidgetAwayTeam) {
        this.matchWidgetAwayTeam = matchWidgetAwayTeam;
    }


   public String getTeam1Flag() throws JSONException {
       if(matchWidgetHomeTeam!=null){
           if(matchWidgetHomeTeam.isNull("team_image")){
               return "";
           }
           return matchWidgetHomeTeam.getString("team_image");
       }else{
           return  "";
       }

    }

    public String getTeam2Flag() throws JSONException {
        if(matchWidgetAwayTeam.isNull("team_image")){
            return "";
        }
        return matchWidgetAwayTeam.getString("team_image");
    }

    public String getVenue() throws JSONException {
        if(jsonObject.isNull("venue")){
            return  "";
        }
        return jsonObject.getString("venue");
    }

    public String getMatchNumber() throws JSONException {
        if(jsonObject.isNull("match_number")){
            return  "";
        }
        return jsonObject.getString("match_number");
    }

    public String getMatchFormat() throws JSONException {
        return jsonObject.getString("match_format");
    }

    public String getTeam1Score() throws JSONException {
        return matchWidgetHomeTeam.getString("runs");

    }

    public String getTeam2Score() throws JSONException {
        return matchWidgetAwayTeam.getString("runs");
    }

    public String getScore(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("score");
    }

    public String getOversTeam1() throws JSONException {
        return matchWidgetHomeTeam.getString("overs");
    }

    public String getWicketsTeam1() throws JSONException {
        return matchWidgetHomeTeam.getString("wickets");
    }

    public String getOversTeam2() throws JSONException {
        return matchWidgetAwayTeam.getString("overs");
    }

    public String getWicketsTeam2() throws JSONException {
        return matchWidgetAwayTeam.getString("wickets");
    }



    public String getMatchResult() throws JSONException {
        if(jsonObject.isNull("match_result")){
            return "";
        }
        return jsonObject.getString("match_result");
    }
    public String getToss() throws JSONException {
        if(jsonObject.isNull("toss"))
        {
            return "";
        }
        return jsonObject.getString("toss");
    }
    public String getShortName() throws JSONException {
        if(jsonObject.isNull("short_name"))
        {
            return "";
        }
        return jsonObject.getString("short_name");
    }
    public String getWinnerTeam(String team) throws JSONException {
        if(jsonObject.isNull(team))
        {
            return "";
        }
        return jsonObject.getString(team);
    }
}
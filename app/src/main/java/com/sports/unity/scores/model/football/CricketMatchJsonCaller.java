package com.sports.unity.scores.model.football;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 8/10/15.
 */
public class CricketMatchJsonCaller extends MatchJsonCaller {

    public int getMatchDateTimeEpoch() throws JSONException {
        return jsonObject.getInt("match_datetime_epoch");
    }

    public String getStatus() throws JSONException {
        return jsonObject.getString("status");
    }

    public String getTeam1() throws JSONException {
        return jsonObject.getString("team_1");
    }

    public String getTeam2() throws JSONException {
        return jsonObject.getString("team_2");
    }

    public String getMatchId() throws JSONException {
        return jsonObject.getString("match_id");
    }

    public String getTeam1Flag() throws JSONException {
        return jsonObject.getString("team_1_flag");
    }

    public String getTeam2Flag() throws JSONException {
        return jsonObject.getString("team_2_flag");
    }

    public String getVenue() throws JSONException {
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

    public JSONObject getTeam1Score() throws JSONException {
        return jsonObject.getJSONObject("team_1_score");
    }

    public JSONObject getTeam2Score() throws JSONException {
        return jsonObject.getJSONObject("team_2_score");
    }

    public String getScore(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("score");
    }

    public String getOvers(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("overs");
    }

    public String getWickets(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("wickets");
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
}
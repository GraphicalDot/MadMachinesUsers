package com.sports.unity.scores.model.football;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;

/**
 * Created by madmachines on 8/10/15.
 */
public class FootballMatchJsonCaller extends JsonObjectCaller {

    public int getMatchDateEpoch() throws JSONException {
        return jsonObject.getInt("match_date_epoch");
    }

    public String getMatchStatus() throws JSONException {
        return jsonObject.getString("match_status");
    }

    public String getHomeTeam() throws JSONException {
        return jsonObject.getString("home_team");
    }

    public String getAwayTeam() throws JSONException {
        return jsonObject.getString("away_team");
    }

    public Integer getMatchId() throws JSONException {
        return jsonObject.getInt("match_id");
    }

    public Integer getLeagueId() throws JSONException {
        return jsonObject.getInt("league_id");
    }

    public String getMatchDate() throws JSONException {
        return jsonObject.getString("match_date");
    }

    public String getAwayTeamFlag() throws JSONException {
        return jsonObject.getString("away_team_flag");
    }

    public String getHomeTeamFlag() throws JSONException {
        return jsonObject.getString("home_team_flag");
    }

    public String getMatchTime() throws JSONException {
        return jsonObject.getString("match_time");
    }

    public String getStadium() throws JSONException {
        return jsonObject.getString("stadium");
    }

    public String getAwayTeamScore() throws JSONException {
        return jsonObject.getString("away_team_score");
    }

    public String getHomeTeamScore() throws JSONException {
        return jsonObject.getString("home_team_score");
    }

    public boolean isLive() throws JSONException {
        return jsonObject.getBoolean("live");
    }

}
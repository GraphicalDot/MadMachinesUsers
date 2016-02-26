package com.sports.unity.scores.model.football;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;

/**
 * Created by madmachines on 8/10/15.
 */
public class FootballMatchJsonCaller extends MatchJsonCaller {

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

    public String getLeagueId() throws JSONException {
        return jsonObject.getString("league_id");
    }

    public String getAwayTeamFlag() throws JSONException {
        return jsonObject.getString("away_team_flag");
    }

    public String getHomeTeamFlag() throws JSONException {
        return jsonObject.getString("home_team_flag");
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

    public String getTeam1Id() throws JSONException {
        return jsonObject.getString("home_team_id");
    }
    public String getTeam2Id() throws JSONException {
        return jsonObject.getString("away_team_id");
    }


}
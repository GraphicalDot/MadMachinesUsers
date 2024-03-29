package com.sports.unity.scoredetails.cricketdetail.JsonParsers;

import com.google.gson.JsonObject;
import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 21/3/16.
 */
public class CompletedCricketMatchSummaryParser extends JsonObjectCaller {

    private JSONObject cricketSummary;
    private JSONObject manOfTheMatch;
    private JSONObject batting;
    private JSONObject umpires;
    private JSONObject bowling;

    public JSONObject getMatchSummary() throws JSONException {
        return jsonObject.getJSONObject("summary");
    }

    public void setCricketSummary(JSONObject cricketSummary) {
        this.cricketSummary = cricketSummary;
    }

    public JSONObject getManOfMatchDetails() throws JSONException {
        return cricketSummary.getJSONObject("man_of_the_match");
    }

    public void setManOfTheMatch(JSONObject manOfTheMatch) {
        this.manOfTheMatch = manOfTheMatch;
    }

    public String getPlayerName() throws JSONException {
        if (!manOfTheMatch.isNull("name")) {
            return manOfTheMatch.getString("name");
        } else {
            return "";
        }
    }

    public String getPlayerId() throws JSONException {
        if (!manOfTheMatch.isNull("player_id")) {
            return manOfTheMatch.getString("player_id");
        } else {
            return "";
        }
    }

    public JSONObject getBattingDetails() throws JSONException {
        return manOfTheMatch.getJSONObject("batting");

    }

    public void setBatting(JSONObject batting) {
        this.batting = batting;
    }

    public String getBalls() throws JSONException {
        if (!batting.isNull("balls")) {
            return batting.getString("balls");
        } else {
            return "";
        }
    }

    public String getruns() throws JSONException {
        if (!batting.isNull("runs") && batting.getInt("runs") > 0) {
            return batting.getString("runs");
        } else {
            return bowling.getString("runs");
        }
    }

    public String getstrikerate() throws JSONException {
        if (!batting.isNull("strike_rate")) {
            return batting.getString("strike_rate");
        } else {
            return "";
        }
    }

    public JSONObject getUmpireDetails() throws JSONException {
        return cricketSummary.getJSONObject("umpires");
    }

    public void setUmpire(JSONObject umpires) {
        this.umpires = umpires;
    }

    public String getFirstUmpire() throws JSONException {
        if (!umpires.isNull("first_umpire")) {
            return umpires.getString("first_umpire");
        } else {
            return "N/A";
        }
    }

    public String getRefree() throws JSONException {
        if (!umpires.isNull("referee")) {
            return umpires.getString("referee");
        } else {
            return "N/A";
        }
    }

    public String getPlayerImage() throws JSONException {
        if (!manOfTheMatch.isNull("player_image")) {
            return manOfTheMatch.getString("player_image");
        } else {
            return "";
        }
    }

    public JSONObject getBowlingDetails() throws JSONException {
        if ( batting.getInt("runs") == 0 ) {
            return manOfTheMatch.getJSONObject("bowling");
        }
        return null;
    }

    public JSONObject getBowling() {
        return bowling;
    }

    public void setBowling(JSONObject bowling) {
        this.bowling = bowling;
    }

    public String secondFirstUmpire() throws JSONException {
        if (!umpires.isNull("second_umpire")) {
            return umpires.getString("second_umpire");
        } else {
            return "N/A";
        }
    }

}

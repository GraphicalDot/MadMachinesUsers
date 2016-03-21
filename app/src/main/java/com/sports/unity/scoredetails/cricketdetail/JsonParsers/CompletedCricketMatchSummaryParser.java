package com.sports.unity.scoredetails.cricketdetail.JsonParsers;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 21/3/16.
 */
public class CompletedCricketMatchSummaryParser extends JsonObjectCaller {
    private JSONObject cricketSummary;
    public JSONObject getMatchSummary() throws JSONException {
        return jsonObject.getJSONObject("summary");
    }
    public void setCricketSummary(JSONObject cricketSummary) {
        this.cricketSummary = cricketSummary;
    }
    public JSONObject getManOfMatchDetails() throws JSONException {

        return cricketSummary.getJSONObject("man_of_the_match");
    }


}

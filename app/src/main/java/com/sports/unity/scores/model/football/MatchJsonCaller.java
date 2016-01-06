package com.sports.unity.scores.model.football;

import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;

/**
 * Created by madmachines on 6/1/16.
 */
public class MatchJsonCaller extends JsonObjectCaller {

    public String getType() throws JSONException {
        return jsonObject.getString(ScoresJsonParser.SPORTS_TYPE_PARAMETER);
    }

    public String getMatchDate() throws JSONException {
        return jsonObject.getString("match_date");
    }

    public String getMatchTime() throws JSONException {
        return jsonObject.getString("match_time");
    }



}

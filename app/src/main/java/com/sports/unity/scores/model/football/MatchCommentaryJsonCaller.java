package com.sports.unity.scores.model.football;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;

/**
 * Created by madmachines on 8/10/15.
 */
public class MatchCommentaryJsonCaller extends JsonObjectCaller {

    public String getComment() throws JSONException {
        return jsonObject.getString("comment");
    }

    public String getMinute() throws JSONException {
        return jsonObject.getString("minute");
    }

    public String getId() throws JSONException {
        return jsonObject.getString("id");
    }

    public int getMatchId() throws JSONException {
        return jsonObject.getInt("match_id");
    }

}
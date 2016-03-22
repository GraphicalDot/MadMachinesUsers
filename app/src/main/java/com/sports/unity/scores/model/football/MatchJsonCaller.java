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
        return jsonObject.getString("start_date");
    }

    public String getMatchTime() throws JSONException {
        return jsonObject.getString("match_time");
    }

    public String getResult() throws JSONException {
        if(jsonObject.isNull("result")){
            return  "";
        }
        return jsonObject.getString("result");
    }

    public String getTeams1Odds() {
        String odds = null;
        try{
            if (!jsonObject.isNull("team_1_odds"))
            odds = jsonObject.getString("team_1_odds");
            else
                odds = "";
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return odds;
    }

    public String getTeams2Odds() throws JSONException {
        String odds = null;
        try{
            if (!jsonObject.isNull("team_2_odds"))
            odds = jsonObject.getString("team_2_odds");
            else
                odds = "";
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return odds;
    }

}

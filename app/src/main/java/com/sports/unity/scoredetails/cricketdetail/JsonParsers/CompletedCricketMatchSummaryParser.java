package com.sports.unity.scoredetails.cricketdetail.JsonParsers;

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

    public String getPlayerName() throws  JSONException{
        if(!manOfTheMatch.isNull("name")){
            return manOfTheMatch.getString("name");
        }else{return  "";}
    }
public JSONObject getbattingdetails()throws JSONException
{
    return manOfTheMatch.getJSONObject("batting");

}

public void setbatting(JSONObject batting)
{
    this.batting=batting;
}

public String getballs() throws JSONException{

    if (!batting.isNull("balls")){
        return batting.getString("balls");
    }
    else
    {
        return "";
    }
}

    public String getruns() throws JSONException{

        if (!batting.isNull("runs")){
            return batting.getString("runs");
        }
        else
        {
            return "";
        }
    }

    public String getstrikerate() throws JSONException{

        if (!batting.isNull("strike_rate")){
            return batting.getString("strike_rate");
        }
        else
        {
            return "";
        }
    }

 /*   public String getsix() throws JSONException{

        if (!batting.isNull("six")){
            return batting.getString("six");
        }
        else
        {
            return "";
        }
    }*/

    public JSONObject getUmpiredetails() throws JSONException {

        return cricketSummary.getJSONObject("umpires");
    }

    public void setUmpire(JSONObject umpires) {
        this.umpires = umpires;
    }

    public String getfirstumpire() throws  JSONException{
        if(!umpires.isNull("first_umpire")){
            return umpires.getString("first_umpire");
        }else{return  "";}
    }

    public String getreferee() throws  JSONException{
        if(!umpires.isNull("name")){
            return umpires.getString("name");
        }else{return  "";}
    }

}

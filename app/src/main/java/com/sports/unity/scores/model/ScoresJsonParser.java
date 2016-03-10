package com.sports.unity.scores.model;
import com.google.android.gms.maps.model.LatLng;
import com.sports.unity.common.model.MatchDay;
import com.sports.unity.messages.controller.model.PeoplesNearMe;
import com.sports.unity.messages.controller.model.Person;
import com.sports.unity.scoredetails.CommentriesModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amandeep on 30/12/15.
 */
public class ScoresJsonParser {

    public static final String CRICKET = "cricket";
    public static final String FOOTBALL = "football";
    public static final String SPORTS_TYPE_PARAMETER = "type";

    public static ArrayList<JSONObject> parseListOfMatches(String jsonContent){

        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if( success ) {
                list = new ArrayList<>();

                jsonObject = jsonObject.getJSONObject("data");

                JSONArray array = (JSONArray) jsonObject.get(CRICKET);
                for( int index=0; index < array.length(); index++){
                    JSONObject match = array.getJSONObject(index);
                    match.put( SPORTS_TYPE_PARAMETER, CRICKET);
                    list.add( match);
                }

                array = (JSONArray) jsonObject.get(FOOTBALL);
                for( int index=0; index < array.length(); index++){
                    JSONObject match = array.getJSONObject(index);
                    match.put( SPORTS_TYPE_PARAMETER, FOOTBALL);
                    list.add(match);
                }

            } else {
                list.clear();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            list.clear();
        }

        return list;
    }

    public static PeoplesNearMe parseListOfNearByUsers(String jsonContent) {
        PeoplesNearMe peoplesNearMe = new PeoplesNearMe();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            int status = jsonObject.getInt("status");
            String info = jsonObject.getString("info");

            if( status == 200 && info.equalsIgnoreCase("Success") ) {
                JSONArray array = jsonObject.getJSONArray("users");
                int arraySize = array.length();
                for (int index = 0; index < arraySize; index++) {
                    JSONObject personObject = array.getJSONObject(index);
                    Person p = new Person();
                    p.setUsername(personObject.getString("username"));
                    p.setDistance(personObject.getDouble("distance"));
                    p.setPosition(new LatLng(personObject.getDouble("lat"), personObject.getDouble("lng")));
                    String friendship_status = personObject.getString("friendship_status");
                    if (!personObject.isNull("interests") && personObject.getJSONArray("interests").length() > 0) {
                        p.setCommonInterest(true);
                    }
                    if (friendship_status.equalsIgnoreCase("friends")) {
                        p.setFriend(true);
                    }
                    peoplesNearMe.getPersons().add(p);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return peoplesNearMe;
    }

    public static ArrayList<CommentriesModel> parseListOfMatchCommentaries(String jsonContent){
       ArrayList<CommentriesModel> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if( success ) {
                JSONArray array = (JSONArray) jsonObject.get("data");
                for( int index=0; index < array.length(); index++){
                    CommentriesModel commentriesModel = new CommentriesModel();
                    JSONObject object = array.getJSONObject(index);
                    if(object == null){
                        continue;
                    } else {
                        if(!object.isNull("comment")){
                            commentriesModel.setComment(object.getString("comment"));
                        }
                        if(!object.isNull("comment_storing_time")){
                            commentriesModel.setMinute(object.getString("comment_storing_time"));
                        }
                        if(!object.isNull("overs")){
                            commentriesModel.setOver(object.getString("overs"));
                        }
                        if(!object.isNull("minute")){
                            commentriesModel.setMinute(object.getString("minute"));
                        }
                        list.add(commentriesModel);
                    }


                }
            } else {
                list.clear();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            list = null;
        }

        return list;

    }

    public static JSONObject parseScoreDetails(String jsonContent){
        JSONObject scoreDetails = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if( success ) {
                JSONArray array = (JSONArray) jsonObject.get("data");
                if( array.length() == 1 ){
                    scoreDetails = array.getJSONObject(0);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return scoreDetails;
    }

    public static List<MatchDay> parseMatchDays(String jsonContent) {
        List<MatchDay> matchDays = new ArrayList<>();
        try {
            JSONObject responseObj = new JSONObject(jsonContent);
            boolean success = responseObj.getBoolean("success");
            if (success) {
                JSONObject dataJson = responseObj.getJSONObject("data");
                JSONArray cricketJsonArray = dataJson.getJSONArray("cricket");
                int cricketMatchCount = cricketJsonArray.length();
                JSONArray footballJsonArray = dataJson.getJSONArray("football");
                int footballMatchCount = footballJsonArray.length();
                for (int cricket = 0; cricket < cricketMatchCount; cricket++) {
                    JSONObject cricketJson = cricketJsonArray.getJSONObject(cricket);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            matchDays.clear();
        }
        return matchDays;
    }

}

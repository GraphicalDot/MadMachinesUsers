package com.sports.unity.scores.model;

import android.util.Log;

import com.sports.unity.scoredetails.CommentriesModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

    public static ArrayList<JSONObject> parseListOfNearByUsers(String jsonContent){
        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            int status = jsonObject.getInt("status");
            String info = jsonObject.getString("info");

            if( status == 200 && info.equalsIgnoreCase("Success") ) {
                list = new ArrayList<>();
                JSONArray array = (JSONArray) jsonObject.get("users");
                for( int index=0; index < array.length(); index++){
                    list.add( array.getJSONObject(index));
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

}

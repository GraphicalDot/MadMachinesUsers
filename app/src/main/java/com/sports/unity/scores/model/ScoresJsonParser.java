package com.sports.unity.scores.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by amandeep on 30/12/15.
 */
public class ScoresJsonParser {

    public static ArrayList<JSONObject> parseListOfMatches(String jsonContent){

        /*

        "data": [
        {
            'match_date_epoch': 1449081000,
            'match_number': '4th Test Match',
            'match_id': 'indrsa_2015_test_04',
            'team_2': 'South Africa',
            'venue': 'Feroz Shah Kotla, Delhi, India',
            'match_format': 'test',
            'match_date': '2015-12-03',
            'match_time': '04:00+00:00',
            'status': 'completed', 'team_1': 'India'
        }
        ,
        {
            'match_date_epoch': 1449599400,
            'match_number': 'Semi Final Match',
            'match_id': 'ramslamt20_2015_sf1',
            'team_2': 'Cape Cobras',
            'venue': 'Kingsmead, Durban, South Africa',
            'match_format': 't20',
            'match_date': '2015-12-09',
            'match_time': '16:00+00:00',
            'status': 'completed',
            'team_1': 'Dolphins'
        }
        ],

       */

        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            String successMessage = jsonObject.getString("success");
            String errorMessage = jsonObject.getString("error");

            if( successMessage.equalsIgnoreCase("true") ) {
                list = new ArrayList<>();
                JSONArray array = (JSONArray) jsonObject.get("data");
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

}

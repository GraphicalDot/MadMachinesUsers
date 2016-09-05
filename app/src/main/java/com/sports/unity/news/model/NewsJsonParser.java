package com.sports.unity.news.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by amandeep on 30/12/15.
 */
public class NewsJsonParser {

    public static ArrayList<JSONObject> parseListOfNews(String jsonContent) {
        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            String successMessage = jsonObject.getString("success");
            String errorMessage = jsonObject.getString("error");

            if (successMessage.equalsIgnoreCase("true")) {
                list = new ArrayList<>();
                JSONArray array = (JSONArray) jsonObject.get("result");
                for (int index = 0; index < array.length(); index++) {
                    list.add(array.getJSONObject(index));
                }
            } else {
                list.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            list.clear();
        }

        return list;
    }

    public static ArrayList<JSONObject> parseListOfCuratedNews(String jsonContent) {
        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);

            String successMessage = jsonObject.getString("success");
            String errorMessage = jsonObject.getString("error");

            if (successMessage.equalsIgnoreCase("true")) {
                list = new ArrayList<>();
                JSONArray array = (JSONArray) jsonObject.get("carousel");
                for (int index = 0; index < array.length(); index++) {
                    list.add(array.getJSONObject(index));
                }
            } else {
                list.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            list.clear();
        }

        return list;
    }

}

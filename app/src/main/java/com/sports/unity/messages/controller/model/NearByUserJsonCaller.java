package com.sports.unity.messages.controller.model;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;

/**
 * Created by madmachines on 5/1/16.
 */
public class NearByUserJsonCaller extends JsonObjectCaller {

    public String getUsername() throws JSONException {
        return jsonObject.getString("username");
    }

    public double getDistance() throws JSONException {
        return jsonObject.getDouble("distance");
    }

    public double getLatitude() throws JSONException {
        return jsonObject.getDouble("lat");
    }

    public double getLongitude() throws JSONException {
        return jsonObject.getDouble("lng");
    }

}

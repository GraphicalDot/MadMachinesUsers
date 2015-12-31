package com.sports.unity.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by amandeep on 30/12/15.
 */
public class JsonObjectCaller {

    protected JSONObject jsonObject = null;

    public JsonObjectCaller(){
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public Object getValue(String key) throws JSONException {
        return jsonObject.get(key);
    }

}

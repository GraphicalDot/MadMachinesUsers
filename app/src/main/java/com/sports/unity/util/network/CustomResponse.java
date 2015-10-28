package com.sports.unity.util.network;

import android.content.Context;

import com.android.volley.VolleyError;

import java.util.ArrayList;

/**
 * Created by amandeep on 27/10/15.
 */
public abstract class CustomResponse {

    protected ResponseListener responseListener = null;

    public CustomResponse(){

    }

    void addResponseListener(ResponseListener responseListener){
        this.responseListener = responseListener;
    }

    void removeResponseListener(){
        responseListener = null;
    }

    abstract public void responedFromCache();

    abstract public void handleResponse(String content);

    abstract public void onErrorResponse(VolleyError error);

    abstract public void fetchContentFromDB(Context context, String tag);

    abstract public ArrayList<ContentRequest> getCustomRequest(String tag);

    abstract public boolean isContentAvailable();

    abstract public boolean isExpired();

}

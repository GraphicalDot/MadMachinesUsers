package com.sports.unity.util.network;

import com.android.volley.VolleyError;

import java.util.ArrayList;

/**
 * Created by amandeep on 27/10/15.
 */
public abstract class CustomResponse {

    private ResponseListener responseListener = null;

    public CustomResponse(){

    }

    void addResponseListener(ResponseListener responseListener){
        this.responseListener = responseListener;
    }

    void removeResponseListener(){
        responseListener = null;
    }

    abstract void responedFromCache();

    abstract void handleResponse(String content);

    abstract void onErrorResponse(VolleyError error);

    abstract void fetchContentFromDB();

    abstract ArrayList<ContentRequest> getCustomRequest(String tag);

    abstract boolean isContentAvailable();

    abstract boolean isExpired();

}

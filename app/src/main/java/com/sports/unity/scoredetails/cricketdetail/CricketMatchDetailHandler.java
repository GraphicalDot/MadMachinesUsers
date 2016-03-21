package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sports.unity.util.network.VolleyRequestHandler;

import java.util.HashSet;

/**
 * Created by madmachines on 16/2/16.
 */
public class CricketMatchDetailHandler {

    private static final String REQUEST_LISTENER_KEY = "CRICKET_MATCH_DETAIL_SCREEN_LISTENER";
    private static final String REQUEST_TAG = "SUMMARY_TAG";
    private Context context;
    private String matchId = null;
    private String url = "http://52.74.75.79:8080/?match_key=";

    private ContentListener contentListener = null;
    private HashSet<String> requestInProcess = new HashSet<>();
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface ContentListener {

        void handleContent(int responseCode, String content);


    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CricketMatchDetailHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CricketMatchDetailHandler.this.handleErrorResponse(volleyError);
        }
    };
    public void requestCricketMatchSummary() {
        Log.i("Score Detail", "Request Score Details");

        url = url+matchId;
        StringRequest stringRequest = null;
        //RequestQueue queue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        try{
            Log.i("Score Card", "handleResponse: "+response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());
        if(contentListener != null) {
            Log.i("handleErrorResponse: ",volleyError.getMessage() );
        }
    }
}

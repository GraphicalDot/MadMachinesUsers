package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by madmachines on 16/2/16.
 */
public class LivedMatchScoreCardHandler {
    private static final String REQUEST_TAG = "LIVE_CRICKET_MATCH_TAG";
    private static Context mContext;
    private String BASEURL = "http://52.74.75.79:8080/get_cricket_match_scorecard?match_key=";

    private LiveMatchContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static LivedMatchScoreCardHandler getInstance(Context context) {
        LivedMatchScoreCardHandler livedMatchScoreCardHandler = null;
        livedMatchScoreCardHandler = new LivedMatchScoreCardHandler();
        mContext = context;
        return livedMatchScoreCardHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface LiveMatchContentListener {

        void handleContent(JSONObject jsonObject);

    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            LivedMatchScoreCardHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            LivedMatchScoreCardHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestMatchScoreCard(String matchId) {
        Log.i("Score Detail", "Request Score Details");

        String url = BASEURL+matchId;
        StringRequest stringRequest = null;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        queue.add(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        Gson gson = new Gson();
        try{
            JSONObject jsonObject = new JSONObject(response);
            Log.i("Score Card", "handleResponse: ");
            if(jsonObject.getBoolean("success")){
                mContentListener.handleContent(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());
         }

    public void addListener(LiveMatchContentListener contentListener) {
        mContentListener = contentListener;
    }

}

package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchTableHandler {

    private static final String REQUEST_TAG = "COMPLETED_CRICKET_MATCH_TAG";
    private static Context mContext;
    private String BASEURL = "http://52.74.75.79:8080/get_league_standings?league_id=";


    private UpCommingFootballMatchTableContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static UpCommingFootballMatchTableHandler getInstance(Context context) {
        mContext = context;
        UpCommingFootballMatchTableHandler completedMatchScoreCardHandler =  new UpCommingFootballMatchTableHandler();
        return completedMatchScoreCardHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface UpCommingFootballMatchTableContentListener {

        void handleContent(String object);

    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            UpCommingFootballMatchTableHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            UpCommingFootballMatchTableHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestUpcommingMatchTableContent(String leagueId) {
        Log.i("Score Detail", "Request Score Details");

        String url = BASEURL+leagueId;
        StringRequest stringRequest = null;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        queue.add(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        try{
            JSONObject jsonObject = new JSONObject(response);
            Log.i("Score Card", "handleResponse: ");
                mContentListener.handleContent(response);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());
        }

    public void addListener(UpCommingFootballMatchTableContentListener contentListener) {
        mContentListener = contentListener;
    }

}

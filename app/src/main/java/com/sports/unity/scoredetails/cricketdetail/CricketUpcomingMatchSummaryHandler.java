package com.sports.unity.scoredetails.cricketdetail;

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
 * Created by madmachines on 16/2/16.
 */
public class CricketUpcomingMatchSummaryHandler {
    private static final String REQUEST_TAG = "UPCOMMING_MATCH_SUMMARY";
    private static Context mContext;
    private String matchId;
    private String url = " http://52.76.74.188:5400/get_match_summary?player_id=";


    private CricketUpcomingMatchSummaryContentListener mcontentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CricketUpcomingMatchSummaryHandler getInstance(Context context) {
        CricketUpcomingMatchSummaryHandler handler = new CricketUpcomingMatchSummaryHandler();
        mContext = context;
        return handler;
    }

    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface CricketUpcomingMatchSummaryContentListener {

        void handleContent(JSONObject object);


    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CricketUpcomingMatchSummaryHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CricketUpcomingMatchSummaryHandler.this.handleErrorResponse(volleyError);
        }
    };
    public void requestCricketUpcommingMatchSummary() {
        Log.i("Score Detail", "Request Score Details");

        url = url+matchId;
        StringRequest stringRequest = null;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        queue.add(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        try{
            JSONObject object = new JSONObject(response);
            mcontentListener.handleContent(object);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void handleErrorResponse(VolleyError volleyError) {
        try{
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());
        if(mcontentListener != null) {
            Log.i("handleErrorResponse: ",volleyError.getMessage() );
        }}catch (Exception e){e.printStackTrace();}
    }
    public void addListener(CricketUpcomingMatchSummaryContentListener contentListener) {
        mcontentListener = contentListener;
    }
}

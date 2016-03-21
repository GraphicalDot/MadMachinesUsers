package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.VolleyRequestHandler;

import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by madmachines on 16/2/16.
 */
public class CricketUpcomingMatchSummaryHandler {
    private static final String REQUEST_TAG = "UPCOMMING_MATCH_SUMMARY";
    private static Context mContext;

    private String BASEURL = "http://52.74.75.79:8080/get_cricket_match_summary?match_key=";


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

        void handleContent(String object);
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
    public void requestCricketUpcommingMatchSummary(String matchId) {
        Log.i("Score Detail", "Request Score Details");

        String url =  BASEURL+matchId;
        StringRequest stringRequest = null;
        //RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);
        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        try{
             mcontentListener.handleContent(response);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void handleErrorResponse(VolleyError volleyError) {
        try{
            Log.i("Score Card", "handleResponse: ");
            mcontentListener.handleContent(Constants.ERRORRESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addListener(CricketUpcomingMatchSummaryContentListener contentListener) {
        mcontentListener = contentListener;
    }
}

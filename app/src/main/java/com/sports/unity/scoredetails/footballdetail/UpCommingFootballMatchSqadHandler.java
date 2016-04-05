package com.sports.unity.scoredetails.footballdetail;

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
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchSqadHandler {

    private static final String REQUEST_TAG = "COMPLETED_CRICKET_MATCH_TAG";
    private static Context mContext;
    private String BASEURL = Constants.SCORE_BASE_URL+"/get_football_squads?team_1=";

    private String BASEURLFORFOTTBALLSQUAD = Constants.SCORE_BASE_URL+"/get_team_players?team_id=";
    private UpCommingFootballMatchSqadContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static UpCommingFootballMatchSqadHandler getInstance(Context context) {
        mContext = context;
        UpCommingFootballMatchSqadHandler completedMatchScoreCardHandler = new UpCommingFootballMatchSqadHandler();
        return completedMatchScoreCardHandler;
    }

    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }

    public interface UpCommingFootballMatchSqadContentListener {

        void handleContent(String object);

    }

    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            UpCommingFootballMatchSqadHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            UpCommingFootballMatchSqadHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestUpCommingMatchSquad(String team1Id, String team2Id) {
        Log.i("Score Detail", "Request Score Details");

        String url = BASEURL + team1Id + "&team_2=" + team2Id;

        StringRequest stringRequest = null;
        //RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }

    public void requestMatchSquad(String team1Id) {
        Log.i("Score Detail", "Request Score Details");

        String url = BASEURLFORFOTTBALLSQUAD + team1Id;
        StringRequest stringRequest = null;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent, responseListener_ForLoadContent);
        queue.add(stringRequest);
        requestInProcess.add(REQUEST_TAG);
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.i("Score Card", "handleResponse: ");
            if (jsonObject.getBoolean("success")) {
                mContentListener.handleContent(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());
        try {
            Log.i("Score Card", "handleResponse: ");
            mContentListener.handleContent(Constants.ERRORRESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(UpCommingFootballMatchSqadContentListener contentListener) {
        mContentListener = contentListener;
    }

}

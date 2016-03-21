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
public class CompletedFootballMatchLineUpHandler {

    private static final String REQUEST_TAG = "COMPLETED_FOOTBALL_LINEUP_MATCH_TAG";
    private static Context mContext;
    private String BASEURL = "http://52.74.75.79:8080/get_match_squads?match_id=";

    private CompletedMatchContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CompletedFootballMatchLineUpHandler getInstance(Context context) {
        mContext = context;
        CompletedFootballMatchLineUpHandler completedMatchScoreCardHandler =  new CompletedFootballMatchLineUpHandler();
        return completedMatchScoreCardHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface CompletedMatchContentListener {

        void handleContent(String object);

    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CompletedFootballMatchLineUpHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CompletedFootballMatchLineUpHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestCompletdMatchLineUps(String matchId) {
        Log.i("Score Detail", "Request Score Details");

        String url = BASEURL+matchId;
        StringRequest stringRequest = null;
       // RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        try{

            Log.i("Score Card", "handleResponse: ");

                mContentListener.handleContent(response);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());
        try{
            Log.i("Score Card", "handleResponse: ");
            mContentListener.handleContent(Constants.ERRORRESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(CompletedMatchContentListener contentListener) {
        mContentListener = contentListener;
    }

}

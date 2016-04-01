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
public class CompletedMatchScoreCardHandler {
    private static final String REQUEST_TAG = "COMPLETED_CRICKET_MATCH_TAG";
    private static Context mContext;
    private String BASEURL = "http://52.74.75.79:8080/v1/get_match_scorecard?season_key=%s&match_id=%s";

    private CompletedMatchContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CompletedMatchScoreCardHandler getInstance(Context context) {
        mContext = context;
        CompletedMatchScoreCardHandler completedMatchScoreCardHandler = null;
        completedMatchScoreCardHandler = new CompletedMatchScoreCardHandler();
        return completedMatchScoreCardHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface CompletedMatchContentListener {

        void handleContent(String object);
       // void handleError();

    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CompletedMatchScoreCardHandler.this.handleResponse(s);

        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CompletedMatchScoreCardHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestCompletdMatchScoreCard(String seriesId,String matchId) {
        Log.i("Score Detail", "Request Score Details");
        String url = String.format(BASEURL,seriesId,matchId);
        StringRequest stringRequest = null;
        Log.d("CompletedScoreDetails", "requestCompletdMatchScoreCard: "+url);
       // RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
                try{
            Log.i("Score Card", "handleResponse: ");
        if(mContentListener!=null){
            mContentListener.handleContent(response);
        }


        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void handleErrorResponse(VolleyError volleyError) {
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

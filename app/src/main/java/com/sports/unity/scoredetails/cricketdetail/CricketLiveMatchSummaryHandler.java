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
public class CricketLiveMatchSummaryHandler {

    private static final String REQUEST_TAG = "LIVE_SUMMARY_TAG";
    private static Context mContext;
    private String BASEURL = Constants.SCORE_BASE_URL+"/v1/get_cricket_match_summary?season_key=%s&match_id=%s";

    private LiveCricketMatchSummaryContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CricketLiveMatchSummaryHandler getInstance(Context context) {
        CricketLiveMatchSummaryHandler completedMatchScoreCardHandler = null;
        completedMatchScoreCardHandler = new CricketLiveMatchSummaryHandler();
        mContext = context;
        return completedMatchScoreCardHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface LiveCricketMatchSummaryContentListener {

        void handleContent(String content);

    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CricketLiveMatchSummaryHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CricketLiveMatchSummaryHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestLiveMatchSummary(String seriesId,String matchId) {
        Log.i("Score Detail", "Request Score Details");
        String url = String.format(BASEURL, seriesId,matchId);
        StringRequest stringRequest = null;
        //RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {

        try{
            if(response!=null){
                JSONObject jsonObject = new JSONObject(response);
                Log.i("Summary", "handleResponse: ");
                if(jsonObject.getBoolean("success")){
                    Log.i("Score Card",response);
                    mContentListener.handleContent(response);
                }
            }else{
                mContentListener.handleContent(Constants.ERRORRESPONSE);
            }

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
    public void addListener(LiveCricketMatchSummaryContentListener contentListener) {
        mContentListener = contentListener;
    }

}

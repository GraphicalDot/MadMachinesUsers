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
 * Created by madmachines on 22/2/16.
 */
public class CricketCompletedMatchSummaryHandler {

    private static final String REQUEST_TAG = "COMPLETED_SUMMARY_TAG";
    private static Context mContext;
    //private String url = "http://52.74.75.79:8080/get_cricket_match_summary?match_key=";
    private String BASEURL = "http://52.74.75.79:8080/v1/get_cricket_match_summary?season_key=%s&match_id=%s";

    private CricketCompletedMatchSummaryContentListener mcontentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CricketCompletedMatchSummaryHandler getInstance(Context context) {
        CricketCompletedMatchSummaryHandler cricketCompletedMatchSummaryHandler = new CricketCompletedMatchSummaryHandler();
        mContext = context;
        return cricketCompletedMatchSummaryHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface CricketCompletedMatchSummaryContentListener {

        void handleContent(String jsonObject);
        void handleError();

    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CricketCompletedMatchSummaryHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CricketCompletedMatchSummaryHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestCompletedMatchSummary(String seriesId,String matchId) {
        Log.i("Score Detail", "Request Score Details");
         String url = String.format(BASEURL, seriesId,matchId);
        Log.d("Summary Url", "requestCompletedMatchSummary: "+url);
        StringRequest stringRequest = null;
        //RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        try{

            Log.i("Score Card", "handleResponse: ");

                mcontentListener.handleContent(response);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());
        try{
            Log.i("Score Card", "handleResponse: ");
            mcontentListener.handleContent(Constants.ERRORRESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void addListener(CricketCompletedMatchSummaryContentListener contentListener) {
        mcontentListener = contentListener;
    }
}

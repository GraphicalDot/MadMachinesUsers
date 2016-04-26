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
public class CompletedFootballMatchStatHandler {
    private static final String REQUEST_TAG = "COMPLETED_FOOTABLL_MATCH_TAG";
    private static final String BASEURL = Constants.SCORE_BASE_URL+"/get_match_stats?match_id=";

    private static CompletedFootballMatchStatHandler completedFootballMatchStatHandler = null;

    private CompletedFootballMatchContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CompletedFootballMatchStatHandler getInstance(Context context) {
        if( completedFootballMatchStatHandler == null ) {
            completedFootballMatchStatHandler = new CompletedFootballMatchStatHandler();
        }
        return completedFootballMatchStatHandler;
    }

    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }

    public interface CompletedFootballMatchContentListener {

        void handleContent(String content);

    }

    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CompletedFootballMatchStatHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CompletedFootballMatchStatHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestCompledFootabllMatchStat(String matchId) {
        Log.i("Score Detail", "Request Score Details");

        String url = BASEURL+matchId;
        StringRequest stringRequest = null;
        //RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);
    }
    private void handleResponse(String response) {
        try{
            Log.i("Score Card", "handleResponse: ");
            if( mContentListener != null ) {
                mContentListener.handleContent(response);
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

    public void addListener(CompletedFootballMatchContentListener contentListener) {
        mContentListener = contentListener;
    }

}

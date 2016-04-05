package com.sports.unity.playerprofile.cricket;

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

import java.util.HashSet;

/**
 * Created by madmachines on 16/2/16.
 */
public class CricketPlayerMatchStatHandler {

    private static final String REQUEST_TAG = "CRICKET_PLAYER_STATS_TAG";
    private static Context mContext;
    private String BASEURL = Constants.SCORE_BASE_URL+"/v1/get_player_stats?player_id=";


    private CricketPlayerMatchStatContentListener mContentListener = null;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CricketPlayerMatchStatHandler getInstance(Context context) {
        mContext = context;
        CricketPlayerMatchStatHandler handler = new CricketPlayerMatchStatHandler();
        return handler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface CricketPlayerMatchStatContentListener {

        void handleContent(String content);


    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CricketPlayerMatchStatHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CricketPlayerMatchStatHandler.this.handleErrorResponse(volleyError);
        }
    };
    public void requestData(String playerId) {
        Log.i("Score Detail", "Request Score Details");
        String url =  BASEURL+playerId;
        StringRequest stringRequest = null;
        //RequestQueue queue = Volley.newRequestQueue(mContext);
        stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent,responseListener_ForLoadContent);
        VolleyRequestHandler.getInstance().addToRequestQueue(stringRequest);

        requestInProcess.add(REQUEST_TAG);


    }
    private void handleResponse(String response) {
        try{
            Log.i("Score Card", "handleResponse: "+response.toString());
            mContentListener.handleContent(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleErrorResponse(VolleyError volleyError) {

        try{
            Log.i("Score Card", "handleResponse: " + Constants.ERRORRESPONSE);
            mContentListener.handleContent(Constants.ERRORRESPONSE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(CricketPlayerMatchStatContentListener contentListener) {
        mContentListener = contentListener;
    }

}

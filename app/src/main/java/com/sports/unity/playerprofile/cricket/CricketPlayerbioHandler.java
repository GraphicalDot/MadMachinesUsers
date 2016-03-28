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
public class CricketPlayerbioHandler {

    private static final String REQUEST_TAG = "CRICKET_PLAYER_BIO_TAG";
    private static Context mContext;
    private String BASEURL = "http://52.76.74.188:5400/get_player_stats?player_id=";


    private CricketPlayerbioContentListener mContentListener = null;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static CricketPlayerbioHandler getInstance(Context context) {
        mContext = context;
        CricketPlayerbioHandler handler = new CricketPlayerbioHandler();
        return handler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface CricketPlayerbioContentListener {

        void handleContent(String content);


    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            CricketPlayerbioHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            CricketPlayerbioHandler.this.handleErrorResponse(volleyError);
        }
    };
    public void requestData(String playerId) {
        String url = BASEURL+playerId;
        Log.i("Get player Bio Request", url);
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
            Log.i("Score Card", "handleResponse: ");
            mContentListener.handleContent(Constants.ERRORRESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(CricketPlayerbioContentListener contentListener) {
        mContentListener = contentListener;
    }

}

package com.sports.unity.gcm;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sports.unity.BuildConfig;
import com.sports.unity.util.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by madmachines on 17/3/16.
 */
public class TokenRegistrationHandler {
    private static final String REQUEST_TAG = "TOKEN_TAG";
    private static Context mContext;

    private String SET_ANDROID_TOKEN = "set_android_token";
    private String REMOVE_ANDROID_TOKEN ="remove_android_token";
    private static final  String  USER_REGISTER_MATCH ="user_register_match";
    private String USER_UNREGISTER_MATCH ="user_unregister_match";

    private TokenRegistrationContentListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static TokenRegistrationHandler getInstance(Context context) {
        mContext = context;
        TokenRegistrationHandler tokenRegistrationHandler = null;
        tokenRegistrationHandler = new TokenRegistrationHandler();
        return tokenRegistrationHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface TokenRegistrationContentListener {

        void handleContent(String content);


    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            TokenRegistrationHandler.this.handleResponse(s);

        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            TokenRegistrationHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void registrerToken(final String username, final String password, final String token, final String uuid) {
        Log.i("Register Token", "Register Token");
        String url = getGeneratedUrl(SET_ANDROID_TOKEN);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener_ForLoadContent,responseListener_ForLoadContent){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
            Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("token", token);
                params.put("apk_version", BuildConfig.VERSION_NAME);
                params.put("udid", uuid);
          return params;
        };
        };
        queue.add(stringRequest);
        requestInProcess.add(REQUEST_TAG);
    }

    public void removeToken(String requestTag,final String username, final String password, final String udid) {
        Log.i("Register Token", "Register Token");
        String url = getGeneratedUrl(REMOVE_ANDROID_TOKEN);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener_ForLoadContent,responseListener_ForLoadContent){
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("apk_version", BuildConfig.VERSION_NAME);
                params.put("udid", udid);
                return params;
            };
        };
        queue.add(stringRequest);
        requestInProcess.add(REQUEST_TAG);
    }

 private String getGeneratedUrl(String requestEndPoint) {

        return BuildConfig.SCORES_BASE_URL+requestEndPoint;
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
        try{
            Log.i("Score Card", "handleResponse: "+volleyError.getMessage());
            mContentListener.handleContent(Constants.ERRORRESPONSE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void addListener(TokenRegistrationContentListener contentListener) {
        mContentListener = contentListener;
    }
}

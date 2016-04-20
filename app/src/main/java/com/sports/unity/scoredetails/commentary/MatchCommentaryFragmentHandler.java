package com.sports.unity.scoredetails.commentary;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.VolleyRequestHandler;

import java.util.HashSet;

/**
 * Created by madmachines on 16/2/16.
 */
public class MatchCommentaryFragmentHandler {
    private static final String REQUEST_TAG = "MATCH_COMMENTARY_TAG";
    private static Context mContext;
    private String BASEURL = Constants.SCORE_BASE_URL;
    private static final String URL_PARAMS_FOR_CRICKET_COMMENTARY = "v1/get_match_commentary?season_key=%s&match_id=%s";
    private static final String URL_PARAMS_FOR_FOOTBALL_COMMENTARY = "get_football_commentary?match_id=%s";

    private CommentaryListener mContentListener;
    private HashSet<String> requestInProcess = new HashSet<>();

    public static MatchCommentaryFragmentHandler getInstance(Context context) {
        mContext = context;
        MatchCommentaryFragmentHandler completedMatchScoreCardHandler = null;
        completedMatchScoreCardHandler = new MatchCommentaryFragmentHandler();
        return completedMatchScoreCardHandler;
    }
    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }
    public interface CommentaryListener {

        void handleContent(String object);
       // void handleError();

    }
    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_TAG);
            MatchCommentaryFragmentHandler.this.handleResponse(s);

        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_TAG);
            MatchCommentaryFragmentHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void requestMatchCommentary(String seriesId,String matchId,String sportsType) {
        Log.i("Score Detail", "Request Score Details");
        String url = null;
        if (sportsType.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
            BASEURL =  BASEURL+ URL_PARAMS_FOR_CRICKET_COMMENTARY;
            url = String.format(BASEURL, seriesId, matchId);
        } else if (sportsType.equalsIgnoreCase(ScoresJsonParser.FOOTBALL)) {
            BASEURL = BASEURL+URL_PARAMS_FOR_FOOTBALL_COMMENTARY;
            url = String.format(BASEURL, matchId);
        }

        StringRequest stringRequest = null;
        Log.d("CompletedScoreDetails", "requestCompletdMatchScoreCard: "+url);
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
            Log.i("Score Card", "handleResponse: " + volleyError.getMessage());
            if(mContentListener!=null){
                mContentListener.handleContent(Constants.ERRORRESPONSE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addListener(CommentaryListener contentListener) {
        mContentListener = contentListener;
    }
}

package com.sports.unity.scores.model;

import com.android.volley.VolleyError;
import com.sports.unity.util.network.VolleyRequestHandler;
import com.sports.unity.util.network.VolleyResponseListener;
import com.sports.unity.util.network.VolleyTagRequest;

import java.util.HashMap;

/**
 * Created by amandeep on 29/12/15.
 */
public class ScoresContentHandler {

    private static final String SCORES_BASE_URL = "http://52.74.142.219:8080/";

    private static final String URL_PARAMS_FOR_LIST_OF_MATCHES = "get_football_upcoming_fixtures";

//    private static final String REQUEST_KEY_LIST_OF_MATCHES = "List_Of_Matches";
//    private static final String REQUEST_KEY_MATCH_DETAILS = "Match_Detail:";
//    private static final String REQUEST_KEY_MATCH_COMMENTARY = "Match_COMMENTARY:";

    private static ScoresContentHandler SCORES_CONTENT_HANDLER = null;

    public static ScoresContentHandler getInstance() {
        if ( SCORES_CONTENT_HANDLER == null ) {
            SCORES_CONTENT_HANDLER = new ScoresContentHandler();
        }
        return SCORES_CONTENT_HANDLER;
    }

    public static void clean(){
        if( SCORES_CONTENT_HANDLER != null ){
            SCORES_CONTENT_HANDLER.cleanUp();
            SCORES_CONTENT_HANDLER = null;
        }
    }

    public interface ContentListener {

        public void handleContent(String tag, String content, int responseCode);

    }

    private HashMap<String, ContentListener> mapOfResponseListeners = new HashMap<>();
    private HashMap<String, String> requestInProcess_RequestTagAndListenerKey = new HashMap<>();

    private ScoresContentHandler(){

    }

    private VolleyResponseListener responseListener = new VolleyResponseListener() {

        @Override
        public void handleResponse(String tag, String response, int responseCode) {
            if( requestInProcess_RequestTagAndListenerKey.containsKey(tag) ) {
                String listenerKey = requestInProcess_RequestTagAndListenerKey.get(tag);
                ContentListener contentListener = mapOfResponseListeners.get(listenerKey);
                if( contentListener != null ) {
                    contentListener.handleContent(tag, response, responseCode);
                } else {
                    //nothing
                }

                requestInProcess_RequestTagAndListenerKey.remove(tag);
            }
        }

    };

    public void addResponseListener(ContentListener responseListener, String listenerKey){
        mapOfResponseListeners.put(listenerKey, responseListener);
    }

    public void removeResponseListener(String listenerKey){
        mapOfResponseListeners.remove(listenerKey);
    }

    public void requestListOfMatches(String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            String url = generateURL( URL_PARAMS_FOR_LIST_OF_MATCHES);
            requestContent(requestTag, listenerKey, url);
        } else {
            //nothing
        }
    }

    public void requestScoresOfMatch(String matchId, String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            String url = generateURL("");
            requestContent(requestTag, listenerKey, url);
        } else {
            //nothing
        }
    }

    public void requestCommentaryOnMatch(String matchId, String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            String url = generateURL("");
            requestContent(requestTag, listenerKey, url);
        } else {
            //nothing
        }
    }

    private void requestContent(String requestTag, String listenerKey, String url){
        if( url != null ) {
            VolleyTagRequest request = new VolleyTagRequest( requestTag, url, responseListener);
            VolleyRequestHandler.getInstance().addToRequestQueue(request);

            requestInProcess_RequestTagAndListenerKey.put(requestTag, listenerKey);
        } else {
            //nothing
        }
    }

    private String generateURL(String parameters){
        StringBuilder stringBuilder = new StringBuilder(SCORES_BASE_URL);
        stringBuilder.append(parameters);
        return stringBuilder.toString();
    }

    private void cleanUp(){
        mapOfResponseListeners.clear();
        requestInProcess_RequestTagAndListenerKey.clear();
    }

}

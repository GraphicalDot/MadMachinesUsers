package com.sports.unity.scores.model;

import com.android.volley.VolleyError;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.VolleyRequestHandler;
import com.sports.unity.util.network.VolleyResponseListener;
import com.sports.unity.util.network.VolleyTagRequest;

import java.util.HashMap;

/**
 * Created by amandeep on 29/12/15.
 */
public class ScoresContentHandler {

    public static final String CALL_NAME_CREATE_USER = "CREATE_USER";
    public static final String CALL_NAME_ASK_OTP = "ASK_OTP";
    public static final String CALL_NAME_MATCHES_LIST = "MATCHES_LIST";
    public static final String CALL_NAME_MATCH_DETAIL = "MATCH_DETAILS";
    public static final String CALL_NAME_MATCH_COMMENTARIES = "MATCH_COMMENTARIES";
    public static final String CALL_NAME_NEAR_BY_USERS = "NEAR_BY_USERS";

    public static final String PARAM_SPORTS_TYPE = "SPORTS_TYPE";
    public static final String PARAM_ID = "ID";

    public static final String PARAM_LATITUDE = "LATITUDE";
    public static final String PARAM_LONGITUDE = "LONGITUDE";
    public static final String PARAM_RADIUS = "RADIUS";

    private static final String URL_CREATE = "http://54.169.217.88/create?";
    public static final String URL_REGISTER = "http://54.169.217.88/register?";
    private static final String URL_REQUEST_OTP = "http://54.169.217.88/create?";
    private static final String URL_NEAR_BY = "http://54.169.217.88/retrieve_nearby_users?";

    private static final String SCORES_BASE_URL = "http://52.74.75.79:8080/";
    private static final String URL_PARAMS_FOR_LIST_OF_MATCHES = "get_all_matches_list";
    private static final String URL_PARAMS_FOR_FOOTBALL_MATCH_DETAIL = "get_football_match_scores?match_id=";
    private static final String URL_PARAMS_FOR_CRICKET_MATCH_DETAIL = "get_cricket_match_scores?match_key=";
    private static final String URL_PARAMS_FOR_CRICKET_COMMENTARY = "get_cricket_match_commentary?match_key=";
    private static final String URL_PARAMS_FOR_FOOTBALL_COMMENTARY = "get_football_commentary?match_id=";

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

    public void requestCall(String callName, HashMap<String, String> parameters, String requestListenerKey, String requestTag){
        if( callName.equals(CALL_NAME_CREATE_USER) ){
            String phoneNumber = parameters.get(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            String otp = parameters.get(Constants.REQUEST_PARAMETER_KEY_AUTH_CODE);
            requestToCreateUser(phoneNumber, otp, requestListenerKey, requestTag);
        } else if( callName.equals(CALL_NAME_ASK_OTP) ){
            String phoneNumber = parameters.get(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            requestForOtp(phoneNumber, requestListenerKey, requestTag);
        } else if( callName.equals(CALL_NAME_NEAR_BY_USERS) ){
            String lat = parameters.get(PARAM_LATITUDE);
            String lng = parameters.get(PARAM_LONGITUDE);
            String radius = parameters.get(PARAM_RADIUS);
            requestNearByUsers(lat, lng, radius, requestListenerKey, requestTag);
        } else if( callName.equals(CALL_NAME_MATCHES_LIST) ){
            requestListOfMatches(requestListenerKey, requestTag);
        } else if( callName.equals(CALL_NAME_MATCH_DETAIL) ){
            String matchId = parameters.get(PARAM_ID);
            String sportsType = parameters.get(PARAM_SPORTS_TYPE);
            requestScoresOfMatch( sportsType, matchId, requestListenerKey, requestTag);
        } else if( callName.equals(CALL_NAME_MATCH_COMMENTARIES) ){
            String matchId = parameters.get(PARAM_ID);
            String sportsType = parameters.get(PARAM_SPORTS_TYPE);
            requestCommentaryOnMatch( sportsType, matchId, requestListenerKey, requestTag);
        }
    }

    public boolean isRequestInProcess(String requestTag){
        return requestInProcess_RequestTagAndListenerKey.containsKey(requestTag);
    }

    private void requestToCreateUser(String phoneNumber, String otp, String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            StringBuilder urlBuilder = new StringBuilder(URL_CREATE);
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            urlBuilder.append("=");
            urlBuilder.append(phoneNumber);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_AUTH_CODE);
            urlBuilder.append("=");
            urlBuilder.append(otp);

            requestContent(requestTag, listenerKey, urlBuilder.toString());
        } else {
            //nothing
        }
    }

    private void requestForOtp(String phoneNumber, String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            StringBuilder urlBuilder = new StringBuilder(URL_REGISTER);
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            urlBuilder.append("=");
            urlBuilder.append(phoneNumber);

            requestContent(requestTag, listenerKey, urlBuilder.toString());
        } else {
            //nothing
        }
    }

    private void requestNearByUsers(String lat, String lng, String radius, String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            StringBuilder urlBuilder = new StringBuilder(URL_NEAR_BY);
            urlBuilder.append("lat=");
            urlBuilder.append(lat);
            urlBuilder.append("&lng=");
            urlBuilder.append(lng);
            urlBuilder.append("&radius=");
            urlBuilder.append(radius);

            requestContent(requestTag, listenerKey, urlBuilder.toString());
        } else {
            //nothing
        }
    }

    private void requestListOfMatches(String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            String url = generateURL( URL_PARAMS_FOR_LIST_OF_MATCHES);
            requestContent(requestTag, listenerKey, url);
        } else {
            //nothing
        }
    }

    private void requestScoresOfMatch(String sportType, String matchId, String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){

            String baseUrl = null;
            if( sportType.equalsIgnoreCase(ScoresJsonParser.CRICKET) ){
                baseUrl = URL_PARAMS_FOR_CRICKET_MATCH_DETAIL;
            } else if( sportType.equalsIgnoreCase(ScoresJsonParser.FOOTBALL) ){
                baseUrl = URL_PARAMS_FOR_FOOTBALL_MATCH_DETAIL;
            }

            String url = generateURL( baseUrl + matchId);
            requestContent(requestTag, listenerKey, url);
        } else {
            //nothing
        }
    }

    private void requestCommentaryOnMatch(String sportType, String matchId, String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){

            String baseUrl = null;
            if( sportType.equalsIgnoreCase(ScoresJsonParser.CRICKET) ){
                baseUrl = URL_PARAMS_FOR_CRICKET_COMMENTARY;
            } else if( sportType.equalsIgnoreCase(ScoresJsonParser.FOOTBALL) ){
                baseUrl = URL_PARAMS_FOR_FOOTBALL_COMMENTARY;
            }

            String url = generateURL( baseUrl + matchId);
            requestContent(requestTag, listenerKey, url);
        }
    }

    private void requestContent(String requestTag, String listenerKey, String url){
        if( url != null ) {
            VolleyTagRequest request = new VolleyTagRequest( requestTag, url, responseListener);
            VolleyRequestHandler.getInstance().addToRequestQueue(request);

            requestInProcess_RequestTagAndListenerKey.put(requestTag, listenerKey);
        }
    }

    private String generateURL(String parameters){
        StringBuilder stringBuilder = new StringBuilder(SCORES_BASE_URL);
        stringBuilder.append(parameters);
        return stringBuilder.toString();
    }
    private String generateFavURL(String baseUrl,String parameters){
        StringBuilder stringBuilder = new StringBuilder(baseUrl);
        stringBuilder.append(parameters);
        return stringBuilder.toString();
    }

    private void cleanUp(){
        mapOfResponseListeners.clear();
        requestInProcess_RequestTagAndListenerKey.clear();
    }
    
    public void requestFavouriteContent(String url,String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            requestContent(requestTag, listenerKey, url);
        }
    }

    public void requestFavouriteSearch(String baseUrl,String params,String listenerKey, String requestTag){
        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
            String url = generateFavURL(baseUrl,params);
            requestContent(requestTag, listenerKey, url);
        }
    }

}

package com.sports.unity.scores.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.sports.unity.BuildConfig;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.VolleyRequestHandler;
import com.sports.unity.util.network.VolleyResponseListener;
import com.sports.unity.util.network.VolleyTagRequest;

import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by amandeep on 29/12/15.
 */
public class ScoresContentHandler {

    public static final String CALL_NAME_CREATE_USER = "CREATE_USER";
    public static final String CALL_NAME_ASK_OTP = "ASK_OTP";
    public static final String CALL_NAME_NEWS_DETAIL = "NEWS_DETAIL";
    public static final String CALL_NAME_MATCHES_LIST = "MATCHES_LIST";
    public static final String CALL_NAME_MATCH_DETAIL = "MATCH_DETAILS";
    public static final String CALL_NAME_MATCH_COMMENTARIES = "MATCH_COMMENTARIES";
    public static final String CALL_NAME_NEAR_BY_USERS = "NEAR_BY_USERS";
    public static final String CALL_NAME_PLAYER_PROFILE = "PLAYER_PROFILE";
    public static final String CALL_NAME_MATCH_TIMELINE = "MATCH_TIMELINE";
    public static final String CALL_NAME_MATCH_LINEUP = "MATCH_LINEUP";
    public static final String CALL_NAME_MATCH_STAT = "MATCH_STAT";

    public static final String PARAM_SPORTS_TYPE = "SPORTS_TYPE";
    public static final String PARAM_ID = "ID";

    public static final String PARAM_USERNAME = "USERNAME";
    public static final String PARAM_PASSWORD = "PASSWORD";
    public static final String PARAM_LATITUDE = "LATITUDE";
    public static final String PARAM_LONGITUDE = "LONGITUDE";
    public static final String PARAM_RADIUS = "RADIUS";

    public static final String PARAM_NEWS_IMAGE_DPI = "IMAGE_DPI";
    public static final String PARAM_NEWS_ID = "NEWS_ID";
    public static final String URL_REGISTER = "http://" + XMPPClient.SERVER_HOST + "/register?";
    public static final String URL_NEWS = "http://52.76.74.188:8000/mixed?";
    private static final String URL_CREATE = "http://" + XMPPClient.SERVER_HOST + "/create?";
    private static final String URL_REQUEST_OTP = "http://" + XMPPClient.SERVER_HOST + "/create?";
    private static final String URL_NEAR_BY = "http://" + XMPPClient.SERVER_HOST + "/get_nearby_users?";
    private static final String URL_PARAMS_NEWS_IMAGE_DPI = "image_size";
    private static final String URL_PARAMS_NEWS_ID = "news_id";
    private static final String SCORES_BASE_URL = BuildConfig.SCORES_BASE_URL;
    private static final String URL_PARAMS_FOR_LIST_OF_MATCHES = "get_all_matches_list";
    private static final String URL_PARAMS_FOR_FOOTBALL_MATCH_DETAIL = "get_football_match_scores?match_id=";
    private static final String URL_PARAMS_FOR_CRICKET_MATCH_DETAIL = "get_cricket_match_scores?match_key=";
    private static final String URL_PARAMS_FOR_CRICKET_COMMENTARY = "get_cricket_match_commentary?match_key=";
    private static final String URL_PARAMS_FOR_FOOTBALL_COMMENTARY = "get_football_commentary?match_id=";
    private static final String URL_PARAMS_FOR_PLAYER_PROFILE_FOOTBALL = "http://52.76.74.188:5600/get_football_player_profile?player_id=";
    private static final String URL_PARAMS_FOR_LEAGUE_FIXTURES = "get_football_league_specific_fixtures?league_id=";
    private static final String URL_PARAMS_FOR_FOOTBAL_TEAM_FIXTURES = "get_football_team_fixtures?team_id=";
    private static final String URL_PARAMS_FOR_CRICKET_TEAM_FIXTURES ="v1/get_specific_fixtures?team_id=4";
    private static ScoresContentHandler SCORES_CONTENT_HANDLER = null;
    private HashMap<String, ContentListener> mapOfResponseListeners = new HashMap<>();
    private HashMap<String, String> requestInProcess_RequestTagAndListenerKey = new HashMap<>();
    private VolleyResponseListener responseListener = new VolleyResponseListener() {

        @Override
        public void handleResponse(String tag, String response, int responseCode) {
            if (requestInProcess_RequestTagAndListenerKey.containsKey(tag)) {
                String listenerKey = requestInProcess_RequestTagAndListenerKey.get(tag);
                ContentListener contentListener = mapOfResponseListeners.get(listenerKey);
                if (contentListener != null) {
                    contentListener.handleContent(tag, response, responseCode);
                } else {
                    //nothing
//=======
//            if( requestInProcess_RequestTagAndListenerKey.containsKey(tag) ) {
//                String listenerKey = requestInProcess_RequestTagAndListenerKey.get(tag);
//                ContentListener contentListener = mapOfResponseListeners.get(listenerKey);
//                if( contentListener != null ) {
//                    contentListener.handleContent(tag, response, responseCode);
//>>>>>>> team2_dev_branch
                }

                requestInProcess_RequestTagAndListenerKey.remove(tag);
            }
        }

    };

    private ScoresContentHandler() {

    }

    public static ScoresContentHandler getInstance() {
        if (SCORES_CONTENT_HANDLER == null) {
            SCORES_CONTENT_HANDLER = new ScoresContentHandler();
        }
        return SCORES_CONTENT_HANDLER;
    }

    public static void clean() {
        if (SCORES_CONTENT_HANDLER != null) {
            SCORES_CONTENT_HANDLER.cleanUp();
            SCORES_CONTENT_HANDLER = null;
        }
    }

    public void addResponseListener(ContentListener responseListener, String listenerKey) {
        mapOfResponseListeners.put(listenerKey, responseListener);
    }

    public void removeResponseListener(String listenerKey) {
        mapOfResponseListeners.remove(listenerKey);
    }

    public void requestCall(String callName, HashMap<String, String> parameters, String requestListenerKey, String requestTag) {
        if (callName.equals(CALL_NAME_CREATE_USER)) {
            String phoneNumber = parameters.get(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            String otp = parameters.get(Constants.REQUEST_PARAMETER_KEY_AUTH_CODE);
            String apk_version = parameters.get(Constants.REQUEST_PARAMETER_KEY_APK_VERSION);
            String udid = parameters.get(Constants.REQUEST_PARAMETER_KEY_UDID);
            requestToCreateUser(phoneNumber, otp, apk_version, udid, requestListenerKey, requestTag);
        } else if (callName.equals(CALL_NAME_ASK_OTP)) {
            String phoneNumber = parameters.get(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            String apk_version = parameters.get(Constants.REQUEST_PARAMETER_KEY_APK_VERSION);
            String udid = parameters.get(Constants.REQUEST_PARAMETER_KEY_UDID);
            requestForOtp(phoneNumber, apk_version, udid, requestListenerKey, requestTag);
        } else if (callName.equals(CALL_NAME_NEWS_DETAIL)) {
            String imageDpi = parameters.get(PARAM_NEWS_IMAGE_DPI);
            String newsId = parameters.get(PARAM_NEWS_ID);
            requestForNewsDetail(imageDpi, newsId, requestListenerKey, requestTag);
        } else if (callName.equals(CALL_NAME_NEAR_BY_USERS)) {
            String lat = parameters.get(PARAM_LATITUDE);
            String lng = parameters.get(PARAM_LONGITUDE);
            String radius = parameters.get(PARAM_RADIUS);
            String apk_version = parameters.get(Constants.REQUEST_PARAMETER_KEY_APK_VERSION);
            String udid = parameters.get(Constants.REQUEST_PARAMETER_KEY_UDID);
            String username = parameters.get(PARAM_USERNAME);
            String password = parameters.get(PARAM_PASSWORD);
            requestNearByUsers(apk_version, udid, lat, lng, radius, requestListenerKey, requestTag, username, password);
        } else if (callName.equals(CALL_NAME_MATCHES_LIST)) {
            String scoreId = null;
            try {
                scoreId = parameters.get(Constants.INTENT_KEY_ID);
                Log.d("max","inscorecontent.> "+scoreId);
            } catch (Exception e) {
            }

            Log.d("max","inscorecontent.> "+scoreId);
            if (scoreId == null) {
                requestListOfMatches(requestListenerKey, requestTag);
            } else {
                requestListOfMatches(requestListenerKey, requestTag, scoreId);
            }
        } else if (callName.equals(CALL_NAME_MATCH_DETAIL)) {
            String matchId = parameters.get(PARAM_ID);
            String sportsType = parameters.get(PARAM_SPORTS_TYPE);
            requestScoresOfMatch(sportsType, matchId, requestListenerKey, requestTag);
        } else if (callName.equals(CALL_NAME_MATCH_COMMENTARIES)) {
            String matchId = parameters.get(PARAM_ID);
//<<<<<<< HEAD
            String sportsType = parameters.get(PARAM_SPORTS_TYPE);
//            requestCommentaryOnMatch(sportsType, matchId, requestListenerKey, requestTag);
//=======
//            String sportsType = parameters.get(Constants.SPORTS_TYPE);
            requestCommentaryOnMatch(sportsType, matchId, requestListenerKey, requestTag);
        } else if (callName.equals(CALL_NAME_PLAYER_PROFILE)) {
            String playerName = parameters.get(Constants.PLAYER_NAME);
            String sportsType = parameters.get(Constants.SPORTS_TYPE);
            requestPlayerProfile(sportsType, playerName, requestListenerKey, requestTag);
        }

    }

    public void requestCall(String callName, Object requestContent, String requestListenerKey, String requestTag, Context context) {

    }

    public boolean isRequestInProcess(String requestTag) {
        return requestInProcess_RequestTagAndListenerKey.containsKey(requestTag);
    }

    private void requestToCreateUser(String phoneNumber, String otp, String apk_version, String udid, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            StringBuilder urlBuilder = new StringBuilder(URL_CREATE);
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            urlBuilder.append("=");
            urlBuilder.append(phoneNumber);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_AUTH_CODE);
            urlBuilder.append("=");
            urlBuilder.append(otp);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_APK_VERSION);
            urlBuilder.append("=");
            urlBuilder.append(apk_version);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_UDID);
            urlBuilder.append("=");
            urlBuilder.append(udid);
            requestContent(requestTag, listenerKey, urlBuilder.toString());
        } else {
            //nothing
        }
    }

    private void requestForOtp(String phoneNumber, String apk_version, String udid, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            StringBuilder urlBuilder = new StringBuilder(URL_REGISTER);
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER);
            urlBuilder.append("=");
            urlBuilder.append(phoneNumber);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_APK_VERSION);
            urlBuilder.append("=");
            urlBuilder.append(apk_version);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_UDID);
            urlBuilder.append("=");
            urlBuilder.append(udid);
            requestContent(requestTag, listenerKey, urlBuilder.toString());
        } else {
            //nothing
        }
    }

    private void requestForNewsDetail(String imageDpi, String newsId, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            StringBuilder urlBuilder = new StringBuilder(URL_NEWS);
            urlBuilder.append(URL_PARAMS_NEWS_IMAGE_DPI);
            urlBuilder.append("=");
            urlBuilder.append(imageDpi);
            urlBuilder.append("&");
            urlBuilder.append(URL_PARAMS_NEWS_ID);
            urlBuilder.append("=");
            urlBuilder.append(newsId);

            requestContent(requestTag, listenerKey, urlBuilder.toString());
        } else {
            //nothing
        }
    }

    private void requestNearByUsers(String apk_version, String udid, String lat, String lng, String radius, String listenerKey, String requestTag, String username, String password) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            StringBuilder urlBuilder = new StringBuilder(URL_NEAR_BY);
            urlBuilder.append("lat=");
            urlBuilder.append(lat);
            urlBuilder.append("&lng=");
            urlBuilder.append(lng);
            urlBuilder.append("&radius=");
            urlBuilder.append(radius);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_APK_VERSION);
            urlBuilder.append("=");
            urlBuilder.append(apk_version);
            urlBuilder.append("&");
            urlBuilder.append(Constants.REQUEST_PARAMETER_KEY_UDID);
            urlBuilder.append("=");
            urlBuilder.append(udid);
            urlBuilder.append("&");
            urlBuilder.append(PARAM_USERNAME.toLowerCase());
            urlBuilder.append("=");
            urlBuilder.append(username);
            urlBuilder.append("&");
            urlBuilder.append(PARAM_PASSWORD.toLowerCase());
            urlBuilder.append("=");
            urlBuilder.append(password);
            requestContent(requestTag, listenerKey, urlBuilder.toString());
        } else {
            //nothing
        }
    }

    private void requestListOfMatches(String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            String url = generateURL(URL_PARAMS_FOR_LIST_OF_MATCHES);
            Log.d("max", "Score url is-" + url);
            requestContent(requestTag, listenerKey, url);
        } else {
            //nothing
        }
    }

    private void requestListOfMatches(String listenerKey, String requestTag,String favouriteItemJsonString) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            FavouriteItem f=new FavouriteItem(favouriteItemJsonString);
            String url="";
            if(f.getFilterType().equals(Constants.FILTER_TYPE_LEAGUE)) {
                url = generateURL(URL_PARAMS_FOR_LEAGUE_FIXTURES + f.getId());
            }else if(f.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)){
                url=generateURL(URL_PARAMS_FOR_FOOTBAL_TEAM_FIXTURES + f.getId());
            }else{
                url=generateURL(URL_PARAMS_FOR_CRICKET_TEAM_FIXTURES);
            }
            Log.d("max", "Score url is-" + url +"  <TYPE> "+f.getSportsType()+" <filter> "+f.getFilterType());
            requestContent(requestTag, listenerKey, url);
        } else {
            //nothing
        }
    }

    private void requestScoresOfMatch(String sportType, String matchId, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {

            String baseUrl = null;
            if (sportType.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
                baseUrl = URL_PARAMS_FOR_CRICKET_MATCH_DETAIL;
            } else if (sportType.equalsIgnoreCase(ScoresJsonParser.FOOTBALL)) {
                baseUrl = URL_PARAMS_FOR_FOOTBALL_MATCH_DETAIL;
            }

            String url = generateURL(baseUrl + matchId);
            requestContent(requestTag, listenerKey, url);
        }
    }

    private void requestCommentaryOnMatch(String sportType, String matchId, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {

            String baseUrl = null;
            if (sportType.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
                baseUrl = URL_PARAMS_FOR_CRICKET_COMMENTARY;
            } else if (sportType.equalsIgnoreCase(ScoresJsonParser.FOOTBALL)) {
                baseUrl = URL_PARAMS_FOR_FOOTBALL_COMMENTARY;
            }

            String url = generateURL(baseUrl + matchId);
            requestContent(requestTag, listenerKey, url);
        }
    }

    private void requestContent(String requestTag, String listenerKey, String url) {
        if (url != null) {
            Log.i("Request Content", url);

            VolleyTagRequest request = new VolleyTagRequest(requestTag, url, responseListener);
            request.setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECTION_READ_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyRequestHandler.getInstance().addToRequestQueue(request);

            requestInProcess_RequestTagAndListenerKey.put(requestTag, listenerKey);
        } else {
            //nothing
        }
    }
//<<<<<<< HEAD

    private void requestContent(String requestTag, String listenerKey, String url, String requestBody) {
        if (url != null) {
            Log.i("Request Content", url);

            VolleyTagRequest request = new VolleyTagRequest(requestTag, url, requestBody, responseListener);
            request.setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECTION_READ_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//=======
//
//    private void requestContent(String requestTag, String listenerKey, String url){
//        if( url != null ) {
//            VolleyTagRequest request = new VolleyTagRequest( requestTag, url, responseListener);
//>>>>>>> team2_dev_branch
            VolleyRequestHandler.getInstance().addToRequestQueue(request);

            requestInProcess_RequestTagAndListenerKey.put(requestTag, listenerKey);
        }
    }

    private String generateURL(String parameters) {
        StringBuilder stringBuilder = new StringBuilder(SCORES_BASE_URL);
        stringBuilder.append(parameters);
        return stringBuilder.toString();
    }

    private String generateFavURL(String baseUrl, String parameters) {
        StringBuilder stringBuilder = new StringBuilder(baseUrl);
        stringBuilder.append(parameters);
        return stringBuilder.toString();
    }

    private void cleanUp() {
        mapOfResponseListeners.clear();
        requestInProcess_RequestTagAndListenerKey.clear();
    }

    //<<<<<<< HEAD
    public void requestFavouriteContent(String url, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
//=======
//    public void requestFavouriteContent(String url,String listenerKey, String requestTag){
//        if( ! requestInProcess_RequestTagAndListenerKey.containsKey(requestTag) ){
//>>>>>>> team2_dev_branch
            requestContent(requestTag, listenerKey, url);
        }
    }

    public void requestSquadContent(String url, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            requestContent(requestTag, listenerKey, url);
        }
    }

    public void requestFavouriteSearch(String baseUrl, String params, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            String url = generateFavURL(baseUrl, params);
            requestContent(requestTag, listenerKey, url);
        }
    }

    private void requestPlayerProfile(String sportType, String playerName, String listenerKey, String requestTag) {
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
            String url = URL_PARAMS_FOR_PLAYER_PROFILE_FOOTBALL + URLEncoder.encode(playerName);
            Log.i("requestPlayerProfile: ", url);
            requestContent(requestTag, listenerKey, url);
        }
    }

    public interface ContentListener {

        public void handleContent(String tag, String content, int responseCode);

    }

}

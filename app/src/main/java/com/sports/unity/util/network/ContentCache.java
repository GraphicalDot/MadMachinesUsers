package com.sports.unity.util.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.sports.unity.news.model.NewsResponseHandler;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dell on 5/28/2015.
 */
public class ContentCache {

    private static ContentCache contentCache;

    public static synchronized ContentCache getInstance( ) {
        if( contentCache == null ) {
            contentCache = new ContentCache( );
        }
        return contentCache;
    }

    static void clear(){
        if( contentCache != null ){
            contentCache.customResponseHashMap.clear();

            contentCache = null;
        }
    }

    private HashMap<String, CustomResponse> customResponseHashMap = new HashMap<>();

    private ContentCache() {

    }

    private CustomResponse createCustomResponse(String tag){
        CustomResponse customResponse = null;
        if( tag.equals(Constants.NEWS_REQUEST_TAG) ) {
            customResponse = new NewsResponseHandler();
        } else {
            //nothing
        }
        customResponseHashMap.put(tag, customResponse);
        return customResponse;
    }

    void askContent( Context context, String tag, boolean requestingForLatestContent, boolean doNotHaveContent){

        if( ! customResponseHashMap.containsKey(tag) ) {
            createCustomResponse(tag);
        }

        CustomResponse customResponse = customResponseHashMap.get(tag);

        boolean isContentAvailableInCache = false;
        boolean isRefreshRequired = false;

        if ( customResponse.isContentAvailable() ) {
            isContentAvailableInCache = true;
            if (customResponse.isExpired()) {
                isRefreshRequired = true;
            }
        } else {
            isContentAvailableInCache = false;
            isRefreshRequired = true;
        }

        if (isContentAvailableInCache) {

            if (isRefreshRequired) {
                requestContent(tag);
            }

            Log.i("Volley Network", "Available in Cache");
            respond(tag);
        } else {
            Log.i("Volley Network", "Fetch from Storage");

            customResponse.fetchContentFromDB(context, tag);
        }

    }

    CustomResponse getContentResponse( String tag){
        if( customResponseHashMap.containsKey( tag) ){
            //nothing
        } else {
            createCustomResponse(tag);
        }
        return customResponseHashMap.get( tag);
    }

    void requestContent(String tag) {
        Log.i("Volley Network", "Refresh Required");
        CustomResponse customResponse = customResponseHashMap.get(tag);
        ArrayList<ContentRequest> requests = customResponse.getCustomRequest(tag);

        if( requests != null && ! requests.isEmpty() ) {
            ContentRequestHandler contentRequestHandler = ContentRequestHandler.getInstance();
            contentRequestHandler.addToRequestQueue(requests);
        }
    }

    void addResponseListener(String tag, ResponseListener responseListener){
        if( customResponseHashMap.containsKey( tag) ){
            CustomResponse customResponse = customResponseHashMap.get(tag);
            customResponse.addResponseListener(responseListener);
        }
    }

    void removeResponseListener(String tag){
        if( customResponseHashMap.containsKey( tag) ){
            CustomResponse customResponse = customResponseHashMap.get(tag);
            customResponse.removeResponseListener();
        }
    }

    void handleResponse( String tag, String response, VolleyError error) {
        Log.i("Volley Network", "Handle response for tag " + tag);

        if (response != null) {
            respond(tag, response);
        } else {
            respondError(tag, error);
        }
    }

    void respond(String tag){
        if( customResponseHashMap.containsKey( tag) ){
            CustomResponse customResponse = customResponseHashMap.get(tag);
            customResponse.responedFromCache();
        }
        Log.i("Volley Network", "Respond with response for tag " + tag);
    }

    void respond(String tag, String content){
        if( customResponseHashMap.containsKey( tag) ){
            CustomResponse customResponse = customResponseHashMap.get(tag);
            customResponse.handleResponse(content);
        }
        Log.i("Volley Network", "Respond with response for tag " + tag);
    }

    void respondError(String tag, VolleyError error){
        if( customResponseHashMap.containsKey( tag) ){
            CustomResponse customResponse = customResponseHashMap.get(tag);
            customResponse.onErrorResponse(error);
        }
        Log.i("Volley Network", "Respond with error response for tag " + tag);
    }

}

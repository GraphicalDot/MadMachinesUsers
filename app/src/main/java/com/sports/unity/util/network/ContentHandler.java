package com.sports.unity.util.network;

import android.content.Context;

import com.sports.unity.news.model.NewsResponseHandler;
import com.sports.unity.util.Constants;

/**
 * Created by Dell on 5/28/2015.
 */
public class ContentHandler {

    public static void askContent( Context context, String tag, boolean requestingForLatestContent, boolean doNotHaveContent){
        ContentCache contentCache = ContentCache.getInstance();
        contentCache.askContent(context, tag, requestingForLatestContent, doNotHaveContent);
    }

    public static CustomResponse getContentResponse( String tag){
        ContentCache contentCache = ContentCache.getInstance();
        return contentCache.getContentResponse(tag);
    }

    public static void removeResponseListener(String tag){
        ContentCache contentCache = ContentCache.getInstance();
        contentCache.removeResponseListener(tag);
    }

    public static void addResponseListener(String tag, ResponseListener responseListener){
        ContentCache contentCache = ContentCache.getInstance();
        contentCache.addResponseListener( tag, responseListener);
    }

    public static void stopRequests(){
        ContentRequestHandler.clear();
    }

    public static void cleanCache(){
        ContentCache.clear();
    }

    public static void stopAndClean(){
        stopRequests();
        cleanCache();
    }

}

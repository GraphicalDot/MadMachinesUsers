package com.sports.unity.util.network;

import android.content.Context;

/**
 * Created by Dell on 5/28/2015.
 */
public class ContentHandler {

    public static void askContent( Context context, String tag, CustomResponse customResponse){
        ContentCache contentCache = ContentCache.getInstance();
        contentCache.addCustomResponse(tag, customResponse);
    }

    public static void askContent( Context context, String tag){
        ContentCache contentCache = ContentCache.getInstance();
        contentCache.askContent(context, tag);
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

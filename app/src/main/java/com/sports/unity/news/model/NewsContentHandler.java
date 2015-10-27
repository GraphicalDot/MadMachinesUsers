package com.sports.unity.news.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by madmachines on 27/10/15.
 */
public class NewsContentHandler {

    public static void fetchLatestContent( Context context, ArrayList<String> filter, NewsResponseListener newsResponseListener){
        NewsContentCache newsContentCache = NewsContentCache.getInstance();
        newsContentCache.fetchLatestContent(context, filter, newsResponseListener);
    }

    public static void fetchMoreContent( Context context, ArrayList<String> filter, NewsResponseListener newsResponseListener){
        NewsContentCache newsContentCache = NewsContentCache.getInstance();
        newsContentCache.fetchMoreContent(context, filter, newsResponseListener);
    }

    public static void stopRequests(){

    }

    public static void clearAll(){

    }

}

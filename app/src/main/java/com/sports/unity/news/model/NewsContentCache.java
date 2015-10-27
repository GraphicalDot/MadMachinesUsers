package com.sports.unity.news.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by madmachines on 27/10/15.
 */
public class NewsContentCache {

    private static NewsContentCache NEWS_CONTENT_CACHE = null;

    private int skipLimit=0;
    private int loadLimit=10;

    private int volleyPendingRequests=0;

    synchronized public static NewsContentCache getInstance(){
        if( NEWS_CONTENT_CACHE == null ){
            NEWS_CONTENT_CACHE = new NewsContentCache();
        }
        return NEWS_CONTENT_CACHE;
    }

    private ArrayList<News> allContent = null;

    private NewsContentCache(){

    }

    void fetchLatestContent(Context context, ArrayList<String> filter, NewsResponseListener newsResponseListener){
        skipLimit = 0;

        StringRequest stringRequest = null;
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = null;
        for(int i=0;i<filter.size();i++) {
            url = getRequestUrl(filter.get(i));
            stringRequest = new StringRequest( Request.Method.GET, url, newsResponseListener, newsResponseListener);
            queue.add(stringRequest);
        }
        volleyPendingRequests = filter.size();
    }

    void fetchMoreContent(Context context, ArrayList<String> filter, NewsResponseListener newsResponseListener){
        skipLimit += loadLimit;

        StringRequest stringRequest = null;
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = null;
        for(int i=0;i<filter.size();i++) {
            url = getRequestUrl(filter.get(i));
            stringRequest = new StringRequest( Request.Method.GET, url, newsResponseListener, newsResponseListener);
            queue.add(stringRequest);
        }
        volleyPendingRequests = filter.size();
    }

    void clearCache(){

    }

    private String getRequestUrl(String filter){
        StringBuilder stringBuilder = new StringBuilder(Constants.URL_NEWS_CONTENT);
        stringBuilder.append("skip=0&limit=");
        stringBuilder.append(loadLimit);
        stringBuilder.append("&image_size=hdpi&type=");
        stringBuilder.append(filter);
        return stringBuilder.toString();
    }

}

package com.sports.unity.util.network;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

import java.util.ArrayList;

/**
 * Created by Dell on 5/25/2015.
 */
public class ContentRequestHandler {

    private static ContentRequestHandler CONTENT_HANDLER;
    private static int THREAD_POOL_SIZE = 3;
    private static boolean SHOULD_CACHE_RESPONSE = false;

    private RequestQueue mRequestQueue;

    private ContentRequestHandler() {
        mRequestQueue = getRequestQueue();

    }

    public static synchronized ContentRequestHandler getInstance() {
        if (CONTENT_HANDLER == null) {
            CONTENT_HANDLER = new ContentRequestHandler();
        }
        return CONTENT_HANDLER;
    }

    static void clear(){
        if( CONTENT_HANDLER != null ){
            CONTENT_HANDLER.stop();
            CONTENT_HANDLER = null;
        }
    }

    RequestQueue getRequestQueue() {
        if ( mRequestQueue == null ) {

            HttpStack stack = new HurlStack();
            Network network = new BasicNetwork(stack);

            RequestQueue queue = new RequestQueue( new NoCache(), network, THREAD_POOL_SIZE);
            queue.start();

            mRequestQueue = queue;
        }
        return mRequestQueue;
    }

    void addToRequestQueue(ArrayList<ContentRequest> requests) {
        for( Request request : requests ) {
            request.setShouldCache(SHOULD_CACHE_RESPONSE);

            getRequestQueue().add(request);
        }
    }

    void cancelRequests( String tag){
        getRequestQueue().cancelAll(tag);
    }

    void stop(){
        if( mRequestQueue != null ){
            mRequestQueue.stop();
            mRequestQueue = null;
        }
    }

}

package com.sports.unity.util.network;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

/**
 * Created by amandeep on 29/12/15.
 */
public class VolleyRequestHandler {

    private static final int THREAD_POOL_SIZE = 2;
    private static final boolean SHOULD_CACHE_RESPONSE = false;

    private static VolleyRequestHandler VOLLEY_REQUEST_HANDLER;

    public static synchronized VolleyRequestHandler getInstance() {
        if (VOLLEY_REQUEST_HANDLER == null) {
            VOLLEY_REQUEST_HANDLER = new VolleyRequestHandler();
        }
        return VOLLEY_REQUEST_HANDLER;
    }

    public static void clear(){
        if( VOLLEY_REQUEST_HANDLER != null ){
            VOLLEY_REQUEST_HANDLER.stop();
            VOLLEY_REQUEST_HANDLER = null;
        }
    }

    private RequestQueue mRequestQueue;

    private VolleyRequestHandler() {
        mRequestQueue = getRequestQueue();

    }

    private RequestQueue getRequestQueue() {
        if ( mRequestQueue == null ) {

            HttpStack stack = new HurlStack();
            Network network = new BasicNetwork(stack);

            RequestQueue queue = new RequestQueue( new NoCache(), network, THREAD_POOL_SIZE);
            queue.start();

            mRequestQueue = queue;
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setShouldCache( SHOULD_CACHE_RESPONSE);

        getRequestQueue().add(req);
    }

    public void cancelRequests( String tag){
        getRequestQueue().cancelAll(tag);
    }

    public void stop(){
        if( mRequestQueue != null ){
            mRequestQueue.stop();
            mRequestQueue = null;
        }
    }

}

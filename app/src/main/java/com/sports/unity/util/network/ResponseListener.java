package com.sports.unity.util.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by Dell on 6/10/2015.
 */
public interface ResponseListener  extends Response.Listener<Object>{

    public void onErrorResponse(VolleyError error);

}

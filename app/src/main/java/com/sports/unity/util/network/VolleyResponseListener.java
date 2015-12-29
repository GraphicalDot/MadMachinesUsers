package com.sports.unity.util.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by amandeep on 29/12/15.
 */
public interface VolleyResponseListener {

    public void handleResponse(String tag, String reponse, VolleyError error);

}

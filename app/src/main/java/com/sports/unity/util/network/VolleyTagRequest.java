package com.sports.unity.util.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by amandeep on 29/12/15.
 */
public class VolleyTagRequest extends Request<String> {

    private String tag;
    private VolleyResponseListener responseListener = null;

    public VolleyTagRequest(String tag, String url, VolleyResponseListener responseListener){
        super(Method.GET, url, null);

        setTag(tag);

        this.tag = tag;
        this.responseListener = responseListener;
    }

    public String getTag() {
        return tag;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try{
            String parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success( parsed, HttpHeaderParser.parseCacheHeaders(response));
        }catch (Exception ex) {
            return Response.error( new ParseError(ex));
        }

    }

    @Override
    protected void deliverResponse(String response) {
        if(responseListener != null ) {
            responseListener.handleResponse(tag, response, 200);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if(responseListener != null ) {
            responseListener.handleResponse(tag, null, 0);
        }
    }

}

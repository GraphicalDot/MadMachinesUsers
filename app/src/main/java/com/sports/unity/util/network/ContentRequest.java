package com.sports.unity.util.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by Dell on 5/28/2015.
 */
public class ContentRequest extends Request<String> {

    private String contentTag;

    public ContentRequest(String contentTag, String url){
        super(Method.GET, url, null);

        setTag(contentTag);
        this.contentTag = contentTag;
    }

    public String getContentTag() {
        return contentTag;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try{
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        }catch (Exception ex) {
            return Response.error( new ParseError(ex));
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        ContentCache contentCache = ContentCache.getInstance();
        contentCache.handleResponse(contentTag, response, null);
    }

    @Override
    public void deliverError(VolleyError error) {
        ContentCache contentCache = ContentCache.getInstance();
        contentCache.handleResponse(contentTag, null, error);
    }

}

package com.sports.unity.news.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sports.unity.Database.NewsDBHelper;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by madmachines on 10/12/15.
 */
public class NewsContentHandler {

    public static final String KEY_BASE_CONTENT = "Base_Content";
    public static final String KEY_SEARCH_CONTENT = "Search_Content";

    private static final int DB_CONTENT_LIMIT = 50;

    private final static String BASE_URL = Constants.URL_NEWS_CONTENT + "skip=0&limit=10&image_size=hdpi";
    private final static String BASE_SUBSET_URL_UP = "&direction=up&timestamp=";
    private final static String BASE_SUBSET_URL_DOWN = "&direction=down&timestamp=";
    private final static String BASE_URL_SEARCH = Constants.URL_NEWS_CONTENT + "image_size=hdpi&search=";

    private static final String REQUEST_CONTENT_TAG = "RequestContent";
    private static final String REQUEST_MORE_CONTENT_TAG = "RequestContentMore";

    private static HashMap<String, NewsContentHandler> MAP_OF_CONTENT_HANDLER = new HashMap<>();

    public static NewsContentHandler getInstance(Context context, String key) {
        NewsContentHandler newsContentHandler = null;
        if( ! MAP_OF_CONTENT_HANDLER.containsKey(key) ) {
            newsContentHandler = new NewsContentHandler(context);
            MAP_OF_CONTENT_HANDLER.put(key, newsContentHandler);
        } else {
            newsContentHandler =    MAP_OF_CONTENT_HANDLER.get(key);
        }

        return newsContentHandler;
    }

    public static void cleanObject() {
        if( MAP_OF_CONTENT_HANDLER != null ){
            MAP_OF_CONTENT_HANDLER.clear();
            MAP_OF_CONTENT_HANDLER = null;
        }
    }

    private interface ResponseListener extends Response.Listener<String>, Response.ErrorListener {

    }

    public interface ContentListener {

        public void handleContent(int responseCode);

    }

    private Context context;

    private ContentListener contentListener = null;
    private HashSet<String> requestInProcess = new HashSet<>();

    private ArrayList<News> filteredNewsArticle = null;
    private ArrayList<String> selectedSports = null;

    private Long timestampFirst;
    private Long timestampLast;
    private String subUrl_HavingSelectedSports = null;

    private boolean searchOn = false;
    private String searchKeyword = null;

    private NewsContentHandler(Context context) {
        this.context = context;
    }

    private ResponseListener responseListener_ForLoadContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_CONTENT_TAG);
            NewsContentHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_CONTENT_TAG);
            NewsContentHandler.this.handleErrorResponse(volleyError);
        }
    };

    private ResponseListener responseListener_ForLoadMoreContent = new ResponseListener() {

        @Override
        public void onResponse(String s) {
            requestInProcess.remove(REQUEST_MORE_CONTENT_TAG);
            NewsContentHandler.this.handleResponse(s);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            requestInProcess.remove(REQUEST_MORE_CONTENT_TAG);
            NewsContentHandler.this.handleErrorResponse(volleyError);
        }
    };

    public void init(ArrayList<News> filteredNewsArticle, boolean searchOn) {
        this.filteredNewsArticle = filteredNewsArticle;

        timestampFirst = null;
        timestampLast = null;

        this.searchOn = searchOn;
        searchKeyword = null;

        selectedSportsChanged();
    }

    public boolean refreshNews(boolean forceRefresh) {
        boolean success = false;
        if(CommonUtil.isInternetConnectionAvailable(context)) {
            success = true;
            requestContent();
        } else if( ! forceRefresh ){
            getDataFromDb();
        }
        return success;
    }

    public void loadMoreNews() {
        requestContentLoadMore();
    }

    public void selectedSportsChanged() {
        selectedSports = UserUtil.getSportsSelected();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < selectedSports.size(); i++) {
            stringBuilder.append( "&type_1=");
            stringBuilder.append( selectedSports.get(i));
        }

        subUrl_HavingSelectedSports = stringBuilder.toString();

        Log.i("News Content Handler", "Selected Sports Changed");
    }

    public void clearContent() {
        Log.i("News Content Handler","Clear Content");
        timestampFirst = null;
        timestampLast = null;
        filteredNewsArticle.clear();
    }

    public void addContentListener(ContentListener contentListener) {
        this.contentListener = contentListener;
    }

    public void removeContentListener() {
        contentListener = null;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    private void requestContent() {
        if( ! requestInProcess.contains(REQUEST_CONTENT_TAG) ) {
            Log.i("News Content Handler", "Request Content");

            StringRequest stringRequest = null;
            RequestQueue queue = Volley.newRequestQueue(context);

            String url = generateUrl(timestampFirst);

            if( url != null ) {
                Log.i("filter", "type" + url);

                stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent, responseListener_ForLoadContent);
                queue.add(stringRequest);

                requestInProcess.add(REQUEST_CONTENT_TAG);
            } else {
                //nothing
            }

        } else {
            //nothing
        }
    }

    private void requestContentLoadMore() {
        if( ! requestInProcess.contains(REQUEST_MORE_CONTENT_TAG) ) {
            Log.i("News Content Handler", "Request Load More Content");

            RequestQueue queue = Volley.newRequestQueue(context);

            StringRequest stringRequest = null;
            String url = generateUrlForLoadMore(timestampLast);

            if( url != null ) {
                stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadMoreContent, responseListener_ForLoadMoreContent);
                queue.add(stringRequest);

                requestInProcess.add(REQUEST_MORE_CONTENT_TAG);
            } else {
                //nothing
            }

        } else {
            //nothing
        }
    }

    private void handleResponse(String response) {
        Log.i("News Content Handler", "Handle Response");

        NewsList newsList = new Gson().fromJson(response, NewsList.class);
        ArrayList<News> list = (ArrayList<News>) newsList.getResult();

        News newsItem = null;

        for (int index = 0; index < list.size(); index++) {
            newsItem = list.get(index);
            if (newsItem.getSummary() == null) {
                list.remove(index);
                index--;
            } else {
                //nothing
            }
        }
        Log.d("News Content Handler", "Response " + response);
        Log.d("News Content Handler", "List Size " + list.size());

        if(!list.isEmpty()) {
            filteredNewsArticle.addAll(list);
            timestampFirst = filteredNewsArticle.get(0).getPublishEpoch();
            timestampLast = filteredNewsArticle.get(filteredNewsArticle.size()-1).getPublishEpoch();
        } else {
            //nothing
        }

        if(searchOn) {
            //nothing
        } else {
            insertIntoDb();
        }

        if(contentListener != null) {
            contentListener.handleContent(1);
        } else {
           //nothing
        }

    }

    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());

        if(contentListener != null) {
            contentListener.handleContent(0);
        } else {
            //nothing
        }
    }

    private void insertIntoDb() {
        Log.i("News Content Handler", "Insert Content To DB");

        if(filteredNewsArticle.size() > DB_CONTENT_LIMIT) {
            ArrayList<News> newsListForInsert = new ArrayList<>();
            for(int i = 0; i < DB_CONTENT_LIMIT; i++) {
                if( ! filteredNewsArticle.isEmpty() ) {
                    newsListForInsert.add(filteredNewsArticle.get(i));
                } else {
                    //nothing
                }

            }
            NewsDBHelper.getInstance(context).saveNewsArticles(newsListForInsert);
        } else {
            NewsDBHelper.getInstance(context).saveNewsArticles(filteredNewsArticle);
        }
    }

    private void getDataFromDb() {
        Log.i("News Content Handler","Fetch Data From DB");

        filteredNewsArticle.addAll(NewsDBHelper.getInstance(context).fetchNewsArticles());

        if(!filteredNewsArticle.isEmpty()) {
            timestampFirst = filteredNewsArticle.get(0).getPublishEpoch();
            timestampLast = filteredNewsArticle.get(filteredNewsArticle.size()-1).getPublishEpoch();
        } else {
            //nothing
        }

        if(contentListener != null) {
            contentListener.handleContent(1);
        } else {
            //nothing
        }
    }

    private String generateUrl(Long timestampFirst) {
        String url = null;
            if (searchOn) {
                Log.d("News Content Handler", "celebrity name : " + getSearchKeyword());
                try {
                    String encodedURL = URLEncoder.encode(getSearchKeyword(), "UTF-8");
                if (timestampFirst == null) {
                    url = BASE_URL_SEARCH + encodedURL;

                } else {
                    url = BASE_URL_SEARCH + encodedURL + BASE_SUBSET_URL_UP + timestampFirst;
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (timestampFirst == null) {
                    url = BASE_URL + subUrl_HavingSelectedSports;
                } else {
                    url = BASE_URL + subUrl_HavingSelectedSports + BASE_SUBSET_URL_UP + timestampFirst;
                }
            }
        Log.d("News Content Handler","Refresh URL : " + url);
        return url;
    }

    private String generateUrlForLoadMore(Long timestampLast) {
        String url = null;
        if(searchOn) {
            if (timestampLast != null) {
                try {
                    String encodedURL = URLEncoder.encode(getSearchKeyword(), "UTF-8");
                    url = BASE_URL_SEARCH +encodedURL+ BASE_SUBSET_URL_DOWN + timestampLast;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
//            url = null;
                //nothing
            }
        } else  {
            if (timestampLast != null) {
                url = BASE_URL + subUrl_HavingSelectedSports + BASE_SUBSET_URL_DOWN + timestampLast;
            } else {
//            url = null;
                //nothing
            }
        }

        Log.d("News Content Handler","Load More URL : " + url);
        return url;
    }
}
package com.sports.unity.news.model;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sports.unity.Database.NewsDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.json.JSONObject;

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

    private final static String BASE_URL = Constants.URL_NEWS_CONTENT + "skip=0&limit=10&image_size=";
    private final static String BASE_SUBSET_URL_UP = "&direction=up&timestamp=";
    private final static String BASE_SUBSET_URL_DOWN = "&direction=down&timestamp=";
  //  private final static String BASE_URL_SEARCH = Constants.URL_NEWS_CONTENT + "image_size=hdpi&search=";
    private final static String BASE_URL_SEARCH = Constants.URL_NEWS_CONTENT + "image_size=";
    private final static String SUBSET_URL_SEARCH ="&search=";

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

        void handleContent(int responseCode);

    }

    private Context context;

    private ContentListener contentListener = null;
    private HashSet<String> requestInProcess = new HashSet<>();

    private ArrayList<JSONObject> filteredNewsArticle = null;
    private ArrayList<String> selectedSports = null;

    private Long timestampFirst;
    private Long timestampLast;
    private String subUrl_HavingSelectedSports = null;

    private boolean searchOn = false;
    private String searchKeyword = null;


    private NewsContentHandler(Context context ) {
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

    public void init(ArrayList<JSONObject> filteredNewsArticle, boolean searchOn) {
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
         if(selectedSports != null) {
             StringBuilder stringBuilder = new StringBuilder();
             for (int i = 0; i < selectedSports.size(); i++) {
                 stringBuilder.append("&type_1=");
                 stringBuilder.append(selectedSports.get(i));
             }

             subUrl_HavingSelectedSports = stringBuilder.toString();
         } else {
             Toast.makeText(context, R.string.select_atleast_one_sport_message,Toast.LENGTH_SHORT).show();
         }
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

            String screen_type = getScreenSize(context);
            String url = generateUrl(timestampFirst,screen_type);

            if( url != null ) {
                Log.i("filter", "type" + url);

                stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadContent, responseListener_ForLoadContent);
                queue.add(stringRequest);

                requestInProcess.add(REQUEST_CONTENT_TAG);
            }

        }
    }

    private void requestContentLoadMore() {
        if( ! requestInProcess.contains(REQUEST_MORE_CONTENT_TAG) ) {
            Log.i("News Content Handler", "Request Load More Content");

            RequestQueue queue = Volley.newRequestQueue(context);

            StringRequest stringRequest = null;
            String screen_type = getScreenSize(context);
            String url = generateUrlForLoadMore(timestampLast, screen_type);

            if( url != null ) {
                stringRequest = new StringRequest(Request.Method.GET, url, responseListener_ForLoadMoreContent, responseListener_ForLoadMoreContent);
                queue.add(stringRequest);

                requestInProcess.add(REQUEST_MORE_CONTENT_TAG);
            }

        }
    }

    public boolean isRequestInProgress(){
        return requestInProcess.contains(REQUEST_CONTENT_TAG);
    }

    private void handleResponse(String response) {
        Log.i("News Content Handler", "Handle Response");

        ArrayList<JSONObject> list = NewsJsonParser.parseListOfNews(response);

        JSONObject newsItem = null;
        NewsJsonCaller newsJsonCaller = new NewsJsonCaller();
        for (int index = 0; index < list.size(); index++) {
            newsItem = list.get(index);
            newsJsonCaller.setJsonObject(newsItem);

            String summary = null;
            try{
                summary = newsJsonCaller.getSummary();
            }catch (Exception ex){
                ex.printStackTrace();
            }

            if ( summary == null) {
                list.remove(index);
                index--;
            }
        }

        Log.d("News Content Handler", "Response " + response);
        Log.d("News Content Handler", "List Size " + list.size());

        if(!list.isEmpty()) {
            try{
                if( filteredNewsArticle.size() > 0 ) {
                    newsJsonCaller.setJsonObject(filteredNewsArticle.get(0));
                    long currentLists_LatestEpoch = newsJsonCaller.getPublishEpoch();

                    newsJsonCaller.setJsonObject(list.get(0));
                    long newLists_LatestEpoch = newsJsonCaller.getPublishEpoch();

                    if( newLists_LatestEpoch >= currentLists_LatestEpoch) {
                        filteredNewsArticle.addAll( 0, list); //add on top
                        Log.i("News Content Handler", "Adding Content from Top");
                    } else {
                        filteredNewsArticle.addAll(list); //add on bottom
                        Log.i("News Content Handler", "Adding Content from Bottom");
                    }
                } else {
                    filteredNewsArticle.addAll(list);
                }

                newsJsonCaller.setJsonObject(filteredNewsArticle.get(0));
                timestampFirst = newsJsonCaller.getPublishEpoch();

                newsJsonCaller.setJsonObject(filteredNewsArticle.get(filteredNewsArticle.size() - 1));
                timestampLast = newsJsonCaller.getPublishEpoch();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        if(!searchOn) {
            insertIntoDb();
        }
        if(contentListener != null) {
            contentListener.handleContent(1);
        }
    }

    private void handleErrorResponse(VolleyError volleyError) {
        Log.i("News Content Handler", "Error Response " + volleyError.getMessage());

        if(contentListener != null) {
            contentListener.handleContent(0);
        }
    }

    private void insertIntoDb() {
        Log.i("News Content Handler", "Insert Content To DB");

        if(filteredNewsArticle.size() > DB_CONTENT_LIMIT) {
            ArrayList<JSONObject> newsListForInsert = new ArrayList<>();
            for(int i = 0; i < DB_CONTENT_LIMIT; i++) {
                if( ! filteredNewsArticle.isEmpty() ) {
                    newsListForInsert.add(filteredNewsArticle.get(i));
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

        NewsJsonCaller newsJsonCaller = new NewsJsonCaller();
        if( ! filteredNewsArticle.isEmpty() ) {
            try {
                newsJsonCaller.setJsonObject(filteredNewsArticle.get(0));
                timestampFirst = newsJsonCaller.getPublishEpoch();

                newsJsonCaller.setJsonObject(filteredNewsArticle.get(filteredNewsArticle.size() - 1));
                timestampLast = newsJsonCaller.getPublishEpoch();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        if(contentListener != null) {
            contentListener.handleContent(1);
        }
    }

    private String generateUrl(Long timestampFirst, String screen_type) {
        String url = null;
            if (searchOn) {
                Log.d("News Content Handler", "celebrity name : " + getSearchKeyword());
                try {
                    String encodedURL = URLEncoder.encode(getSearchKeyword(), "UTF-8");
                    if (timestampFirst == null) {
                        url = BASE_URL_SEARCH + screen_type + SUBSET_URL_SEARCH + encodedURL;

                    } else {
                        url = BASE_URL_SEARCH + screen_type + SUBSET_URL_SEARCH + encodedURL + BASE_SUBSET_URL_UP + timestampFirst;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (timestampFirst == null) {
                    url = BASE_URL + screen_type + subUrl_HavingSelectedSports;
                } else {
                    url = BASE_URL + screen_type + subUrl_HavingSelectedSports + BASE_SUBSET_URL_UP + timestampFirst;
                }
            }
        Log.d("News Content Handler","Refresh URL : " + url);
        return url;
    }

    private String generateUrlForLoadMore(Long timestampLast, String screen_type) {

        String url = null;
        if(searchOn) {
            if (timestampLast != null) {
                try {
                    String encodedURL = URLEncoder.encode(getSearchKeyword(), "UTF-8");
                    url = BASE_URL_SEARCH + screen_type + SUBSET_URL_SEARCH +encodedURL+ BASE_SUBSET_URL_DOWN + timestampLast;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else  {
            if (timestampLast != null) {
                url = BASE_URL + screen_type + subUrl_HavingSelectedSports + BASE_SUBSET_URL_DOWN + timestampLast;
            }
        }

        Log.d("News Content Handler", "Load More URL : " + url);
        return url;
    }

    public String getScreenSize(Context context) {

        float density= context.getResources().getDisplayMetrics().density;

        Log.i("density : ",""+density);
        String screen_type = null;

        if(density == 1.0) {
            screen_type = "mdpi";

        } else if (density == 1.5) {
            screen_type = "mdpi";

        } else if (density == 2.0) {
            screen_type = "hdpi";

        } else {
            screen_type = "hdpi";
        }

        return screen_type;
    }
}
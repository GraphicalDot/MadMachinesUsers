package com.sports.unity.news.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sports.unity.Database.NewsDBHelper;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.ContentCache;
import com.sports.unity.util.network.ContentHandler;
import com.sports.unity.util.network.ContentRequest;
import com.sports.unity.util.network.CustomResponse;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by madmachines on 27/10/15.
 */
public class NewsResponseHandler extends CustomResponse {

    private int skipLimit = 0;
    private int loadLimit = 10;

    private ArrayList<String> filterList = null;

    private ArrayList<News> content = new ArrayList<>();
    private ArrayList<News> contentForAdapter = new ArrayList<>();

    private int requestCallsInProgress = 0;

    private ArrayList<News> parseJson(String response){
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
        return list;
    }

    private String getRequestUrl(String filter){
        StringBuilder stringBuilder = new StringBuilder(Constants.URL_NEWS_CONTENT);
        stringBuilder.append("skip=");
        stringBuilder.append(skipLimit);
        stringBuilder.append("&limit=");
        stringBuilder.append(loadLimit);
        stringBuilder.append("&image_size=hdpi&type=");
        stringBuilder.append(filter);
        return stringBuilder.toString();
    }

    private void insertIntoDb() {
        if(content.size()>50) {
            ArrayList<News> newsListForInsert = new ArrayList<>();
            for(int i=0; i<50; i++) {
                if( ! content.isEmpty() ) {
                    newsListForInsert.add(content.get(i));
                } else {
                    //nothing
                }

            }
            NewsDBHelper.getInstance(null).saveNewsArticles(newsListForInsert);
        } else {
            NewsDBHelper.getInstance(null).saveNewsArticles(content);
        }
    }

    public void setFilterList(ArrayList<String> filterList) {
        this.filterList = filterList;
    }

    private void mergeContent(ArrayList<News> list){
        ArrayList<News> toBeAdded = null;
        if (!content.isEmpty() && !list.isEmpty()) {

            long latestNewsArticleEpoch_AlreadyHave = content.get(0).getPublishEpoch();
            long oldestNewsArticleEpoch_AlreadyHave = content.get(content.size() - 1).getPublishEpoch();
            long latestNewsArticleEpoch = list.get(0).getPublishEpoch();

            if (latestNewsArticleEpoch > latestNewsArticleEpoch_AlreadyHave) {
                toBeAdded = new ArrayList<>();
                for (News news : list) {
                    if (news.getPublishEpoch() > latestNewsArticleEpoch_AlreadyHave) {
                        toBeAdded.add(news);
                    } else {
                        //nothing
                    }
                }
            } else if (latestNewsArticleEpoch < oldestNewsArticleEpoch_AlreadyHave) {
                toBeAdded = new ArrayList<>();
                for (News news : list) {
                    if (news.getPublishEpoch() < oldestNewsArticleEpoch_AlreadyHave) {
                        toBeAdded.add(news);
                    } else {
                        //nothing
                    }
                }
            } else {
                //nothing
            }
        } else {
            toBeAdded = list;
        }

        if (toBeAdded != null) {
            content.addAll(toBeAdded);
        } else {
            //nothing
        }

        Collections.sort(content);
        insertIntoDb();

        contentForAdapter.clear();
        contentForAdapter.addAll( content);
    }

//    public void setSkip_Init(){
//        skipLimit = 0;
//    }
//
//    public void setSkip_More(){
//        skipLimit += loadLimit;
//    }

    @Override
    public void responedFromCache() {
        if( responseListener != null ){
            responseListener.onResponse(contentForAdapter);
        } else {
            //nothing
        }
    }

    @Override
    public void handleResponse(String content) {
        requestCallsInProgress--;

        ArrayList<News> list = parseJson(content);
        mergeContent(list);

        if( requestCallsInProgress == 0 ){
            if( responseListener != null ){
                responseListener.onResponse(contentForAdapter);
            } else {
                //nothing
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        requestCallsInProgress--;

        if( requestCallsInProgress == 0 ){
            if( responseListener != null ){
                responseListener.onErrorResponse(error);
            } else {
                //nothing
            }
        }
    }

    @Override
    public void fetchContentFromDB(final Context context, final String tag) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                ArrayList<News> list = NewsDBHelper.getInstance(context).fetchNewsArticles();
                mergeContent(list);

                if( responseListener != null ){
                    responseListener.onResponse(contentForAdapter);
                } else {
                    //nothing
                }

                ContentHandler.askContent( context, tag, true, false);
            }

        });
        thread.start();
    }

    @Override
    public ArrayList<ContentRequest> getCustomRequest(String tag) {
        ArrayList<ContentRequest> requests = null;
        if( requestCallsInProgress == 0 ) {
            if (filterList != null) {
                requests = new ArrayList<>();
                for (String filter : filterList) {
                    requests.add(new ContentRequest(tag, getRequestUrl(filter)));
                    requestCallsInProgress++;
                }
            }
        } else {
            //request in progress
        }
        return requests;
    }

    @Override
    public boolean isContentAvailable() {
        return false;
    }

    @Override
    public boolean isExpired() {
        return true;
    }

}

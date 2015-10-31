package com.sports.unity.news.controller.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sports.unity.Database.NewsDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.news.model.News;
import com.sports.unity.news.model.NewsList;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.Database.NewsDBHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Edwin on 15/02/2015.
 */
public class NewsFragment extends Fragment implements Response.Listener<String>, Response.ErrorListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private  String type_1="";
    private String url = null;
    private Long timestampFirst;
    private Long timestampLast;

    private int loadLimit = 10;
    private int skipLimit = 0;
    private static int current_page = 1;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private int previousTotal = 0;
    private int volleyPendingRequests = 0;
    private ProgressBar progressBar;

    private ArrayList<News> allNewsArticle = new ArrayList<>();
    private ArrayList<News> filteredNewsArticle = new ArrayList<>();
    private ArrayList<String> filter = null;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(com.sports.unity.R.layout.news, container, false);

        filter = UserUtil.getSportsSelected();

        initViews(v);
        return v;
    }

    private void initViews(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2C84CC"));

        mRecyclerView = (RecyclerView) v.findViewById(com.sports.unity.R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2C84CC"), android.graphics.PorterDuff.Mode.MULTIPLY);

        Log.i("NewsFragment", "initial request call");


        if( CommonUtil.isInternetConnectionAvailable(getActivity()) ) {
            progressBar.setVisibility(View.VISIBLE);
            requestContent();
        } else {
            progressBar.setVisibility(View.GONE);
            filteredNewsArticle=NewsDBHelper.getInstance(getActivity()).fetchNewsArticles();
            displayResult();
        }

        if (mSwipeRefreshLayout != null) {

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Toast.makeText(getActivity(), "Refreshing!", Toast.LENGTH_SHORT).show();
                    //requestDataRefresh();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // getData(url);
                            Log.i("NewsFragment" , "refresh and request content");
                            requestContent();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, 2000);
                }
            });
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mRecyclerView.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();


                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    current_page++;
                    Log.i("Yaeye!", "end called");
                    Log.i("NewsFragment", "request content on load more");

                    requestContentLoadMore();

                    loading = true;
                } else {
                    //nothing
                }
            }
        });


    }

    private void resetScrollFlag(){
        loading = false;
    }



    private void requestContentLoadMore() {
        Log.i("requestContentLoadMore" , "Filter size" +filter.size());
        skipLimit = skipLimit + 10;
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        // progressBar.setVisibility(View.GONE);
        StringRequest stringRequest = null;
        Log.i("requestContentLoadMore" , "Skip limit" +skipLimit);
//        for(int i=0;i<filter.size();i++) {
//
//
//        }
            url = "http://52.74.250.156:8000//mixed?skip="+skipLimit+"&limit=10&image_size=hdpi"+type_1+"&direction=down&timestamp="+timestampLast;

        Log.i("filter","type"+type_1);
        Log.i("filter","type"+url);
            stringRequest = new StringRequest(Request.Method.GET, url, this, this);
            queue.add(stringRequest);

       volleyPendingRequests =1;
    }

    private void requestContent() {

        Log.i("requestContent", "Filter size" + filter.size());
        StringRequest stringRequest = null;
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        Log.i("requestContent", "Skip limit" + skipLimit);
       type_1="";
        for(int i=0;i<filter.size();i++) {
            //url = "http://52.74.250.156:8000//mixed?skip=" + skipLimit + "&limit=" + loadLimit + "&image_size=hdpi&type="+filter.get(i)+"";
          String temp="&type_1="+filter.get(i);
            type_1=type_1+temp;

        }

        Log.i("filter","type"+type_1);
        if(timestampFirst==null) {
            url = "http://52.74.250.156:8000//mixed?skip=0&limit=10&image_size=hdpi"+type_1;
        }else
        {
            url = "http://52.74.250.156:8000//mixed?skip=0&limit=10&image_size=hdpi"+type_1+"&direction=up&timestamp="+timestampFirst;
        }
        Log.i("filter","type"+url);

        stringRequest = new StringRequest(Request.Method.GET, url, this, this);

        queue.add(stringRequest);
        volleyPendingRequests =1;
    }

    @Override
    public void onResponse(String response) {
        Log.i("NewsFragment" , "response on request call");
        volleyPendingRequests--;
        resetScrollFlag();
        progressBar.setVisibility(View.GONE);

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

        Log.i("On Response List.size()", "" + list.size());

      //  ArrayList<News> toBeAdded = null;
      //  if (!filteredNewsArticle.isEmpty() && !list.isEmpty()) {

//            long latestNewsArticleEpoch_AlreadyHave = filteredNewsArticle.get(0).getPublishEpoch();
//            long oldestNewsArticleEpoch_AlreadyHave = filteredNewsArticle.get(filteredNewsArticle.size() - 1).getPublishEpoch();
//            long latestNewsArticleEpoch = list.get(0).getPublishEpoch();

//            if (latestNewsArticleEpoch > latestNewsArticleEpoch_AlreadyHave) {
//                toBeAdded = new ArrayList<>();
//                for (News news : list) {
//                    if (news.getPublishEpoch() > latestNewsArticleEpoch_AlreadyHave) {
//                        toBeAdded.add(news);
//                    } else {
//                        //nothing
//                    }
//                }
//            } else if (latestNewsArticleEpoch < oldestNewsArticleEpoch_AlreadyHave) {
//                toBeAdded = new ArrayList<>();
//                for (News news : list) {
//                    if (news.getPublishEpoch() < oldestNewsArticleEpoch_AlreadyHave) {
//                        toBeAdded.add(news);
//                    } else {
//                        //nothing
//                    }
//                }
//            }
//            else {
//                //nothing
//            }
        if(!list.isEmpty())
        {
            filteredNewsArticle.addAll(list);
            timestampFirst=filteredNewsArticle.get(0).getPublishEpoch();
            timestampLast=filteredNewsArticle.get(filteredNewsArticle.size()-1).getPublishEpoch();
        }

//        else {
//            toBeAdded = list;
//        }
//
//        if (toBeAdded != null) {
//            filteredNewsArticle.addAll(toBeAdded);
//        } else {
//            //nothing
//        }

        Collections.sort(filteredNewsArticle);

        insertIntoDb();

        displayResult();
    }

    public void insertIntoDb()
    {
        if(filteredNewsArticle.size()>50)
        {
            ArrayList<News> newsListForInsert = new ArrayList<>();
            for(int i=0; i<50; i++)
            {
                if( ! filteredNewsArticle.isEmpty() ) {
                    newsListForInsert.add(filteredNewsArticle.get(i));
                } else {
                    //nothing
                }

            }
            NewsDBHelper.getInstance(getActivity()).saveNewsArticles(newsListForInsert);
        }
        else
        {
            NewsDBHelper.getInstance(getActivity()).saveNewsArticles(filteredNewsArticle);
        }
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        resetScrollFlag();
        progressBar.setVisibility(View.GONE);
    }

//    private void finalResult(ArrayList<News> list) {
//        Log.i("all list size()", "" + list.size());
//        allNewsArticle.addAll(list);
//        if( isFilterOn() ){
//            populateFilteredArticles(list);
//        } else {
//
//        }
//
//        Log.i("all news articles size", "" + allNewsArticle.size());
//
//        displayResult();
//    }
//
//    private boolean isFilterOn(){
//        boolean filterON = false;
//        if ( ! filter.isEmpty() ) {
//            filterON = true;
//        } else {
//            //nothing
//        }
//        return filterON;
//    }
//
//    private void filterChanged(){
//        if( isFilterOn() ){
////            filteredNewsArticle = new ArrayList<>();
//            filteredNewsArticle.clear();
//            populateFilteredArticles(allNewsArticle);
//        } else {
//            filteredNewsArticle = allNewsArticle;
//        }
//    }
//
//    private void populateFilteredArticles( ArrayList<News> content){
//        if ( isFilterOn() ) {
//            News newsItem = null;
//            for (int index = 0; index < content.size(); index++)
//            {
//                newsItem = content.get(index);
//                if ( filter.contains(newsItem.getType()) ) {
//                    filteredNewsArticle.add(newsItem);
//                } else {
//                    //nothing
//                }
//            }
//        } else {
//            //nothing
//        }
//
//    }

    private void displayResult() {
        Log.i( "Filtered list size ", "" + filteredNewsArticle.size());
        if( volleyPendingRequests == 0 ) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if ((TinyDB.getInstance(getActivity().getApplicationContext()).getBoolean("check", false))) {
                        if (mAdapter == null) {
                            mAdapter = new NewsMinicardAdapter(NewsFragment.this.filteredNewsArticle, getActivity());
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            if (mAdapter instanceof NewsMinicardAdapter) {
                                Log.i("sportunity", "no change in mini adapter");
//                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.i("sportunity", "creating mini adapter");
                                mAdapter = new NewsMinicardAdapter(NewsFragment.this.filteredNewsArticle, getActivity());
                                mRecyclerView.setAdapter(mAdapter);

                            }
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {
                        if (mAdapter == null) {
                            Log.i("sportunity", "creating news adapter");
                            mAdapter = new NewsAdapter(NewsFragment.this.filteredNewsArticle, getActivity());
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            if (mAdapter instanceof NewsAdapter) {
                                Log.i("sportunity", "no change in news adapter");
//                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.i("sportunity", "creating news adapter");
                                mAdapter = new NewsAdapter(NewsFragment.this.filteredNewsArticle, getActivity());
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_news_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        if ((TinyDB.getInstance(getActivity().getApplicationContext()).getBoolean("check", false)))
            menu.findItem(R.id.mini_cards).setChecked(true);
        else
            menu.findItem(R.id.mini_cards).setChecked(false);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == 999 && data != null ) {
            filter = UserUtil.getSportsSelected();
//            filterChanged();
//            displayResult();
            skipLimit=0;
            type_1="";
            timestampFirst=null;
            timestampLast=null;
           filteredNewsArticle.clear();
           requestContent();
        } else {
            //nothing
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == com.sports.unity.R.id.action_settings) {
            return true;
        }

        if (id == com.sports.unity.R.id.action_filter) {
            Intent i = new Intent(getActivity(), FilterActivity.class);
            startActivityForResult(i, 999);
            return true;
        }

        if (id == R.id.mini_cards) {
            if (item.isChecked()) {
                item.setChecked(false);
                TinyDB.getInstance(getActivity().getApplicationContext()).putBoolean("check", false);
                mAdapter = new NewsAdapter(NewsFragment.this.filteredNewsArticle, getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                item.setChecked(true);
                TinyDB.getInstance(getActivity().getApplicationContext()).putBoolean("check", true);
                mAdapter = new NewsMinicardAdapter(NewsFragment.this.filteredNewsArticle, getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            displayResult();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
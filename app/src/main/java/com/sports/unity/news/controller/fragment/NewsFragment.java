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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.news.controller.activity.NewsSearchActivity;
import com.sports.unity.news.model.News;
import com.sports.unity.news.model.NewsContentHandler;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements NewsContentHandler.ContentListener{

    private NewsContentHandler newsContentHandler;

    private BaseNewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout error;
    private ProgressBar progressBar;

    private boolean loading = true;

    private boolean searchOn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        searchOn = getArguments().getBoolean(Constants.INTENT_KEY_SEARCH_ON);

        View v = inflater.inflate(com.sports.unity.R.layout.news, container, false);
        initViews(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        newsContentHandler.addContentListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        newsContentHandler.removeContentListener();
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

        error = (LinearLayout) v.findViewById(R.id.error);
        error.setVisibility(View.GONE);

        TextView oops=(TextView) error.findViewById(R.id.oops);
        TextView something_wrong=(TextView) error.findViewById(R.id.something_wrong);
        oops.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());
        something_wrong.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());

        if( searchOn == false ) {
            newsContentHandler = NewsContentHandler.getInstance(getActivity().getBaseContext(), NewsContentHandler.KEY_BASE_CONTENT);
        } else {
            something_wrong.setText("No search results found for this search");
            newsContentHandler = NewsContentHandler.getInstance(getActivity().getBaseContext(), NewsContentHandler.KEY_SEARCH_CONTENT);
        }

        addOrUpdateAdapter();

        progressBar.setVisibility(View.VISIBLE);

        if( searchOn == false ) {
            progressBar.setVisibility(View.GONE);
            newsContentHandler.refreshNews(false);
        } else {
            progressBar.setVisibility(View.GONE);
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        Log.i("News Content", "Refresh Call");

                        newsContentHandler.refreshNews(true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                });
            }

        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int visibleThreshold = 5;
            private int previousTotal = 0;

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
                    Log.i("News Content", "Load More Call");

                    newsContentHandler.loadMoreNews();
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

    private void displayResult() {
        Log.i("News Content", "Display Result");

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                ArrayList<News> list = mAdapter.getNews();
                Log.i("Adapter size","Count:" + list.size());
                if(list.size() == 0) {
                    error.setVisibility(View.VISIBLE);
                    if(!searchOn) {
                        Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                    } else {
                        //nothing
                    }
                } else{
                   error.setVisibility(View.GONE);
                    addOrUpdateAdapter();
                }

            }

        });
    }

    private void  addOrUpdateAdapter(){
        boolean flag = TinyDB.getInstance(getActivity()).getBoolean("check", false);

        ArrayList list = null;
        if( flag ){
            if (mAdapter == null) {
                Log.d("News Content", "creating mini adapter");
                list = new ArrayList();
                mAdapter = new NewsMinicardAdapter( list, getActivity());
                mRecyclerView.setAdapter(mAdapter);

                newsContentHandler.init( list, searchOn);
            } else {
                if (mAdapter instanceof NewsMinicardAdapter) {
                    Log.d("News Content", "no change in mini adapter");
                } else {
                    Log.d("News Content", "creating mini adapter");

                    list = mAdapter.getNews();

                    mAdapter = new NewsMinicardAdapter(list, getActivity());
                    mRecyclerView.setAdapter(mAdapter);

                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            if (mAdapter == null) {
                Log.d("News Content", "creating news adapter");
                list = new ArrayList();
                mAdapter = new NewsAdapter(list, getActivity());
                mRecyclerView.setAdapter(mAdapter);

                newsContentHandler.init( list, searchOn);
            } else {
                if (mAdapter instanceof NewsAdapter) {
                    Log.d("News Content", "no change in news adapter");
                } else {
                    Log.d("News Content", "creating news adapter");

                    list = mAdapter.getNews();

                    mAdapter = new NewsAdapter(list, getActivity());
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            list = mAdapter.getNews();
            Log.d( "News Content", "Update Adapter List Object ID " + list);

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_news_menu, menu);

        if ((TinyDB.getInstance(getActivity()).getBoolean("check", false))) {
            menu.findItem(R.id.mini_cards).setChecked(true);
        } else {
            menu.findItem(R.id.mini_cards).setChecked(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( requestCode == 999 && data != null ) {

            newsContentHandler.clearContent();
            mAdapter.notifyDataSetChanged();

            newsContentHandler.selectedSportsChanged();
            boolean success = newsContentHandler.refreshNews(true);

            if(success == false) {
                error.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
            } else {
                error.setVisibility(View.GONE);
            }


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

        if (id == R.id.action_search) {
            Intent intent = new Intent(getActivity(), NewsSearchActivity.class);
            startActivity(intent);
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
                TinyDB.getInstance(getActivity()).putBoolean("check", false);
            } else {
                item.setChecked(true);
                TinyDB.getInstance(getActivity()).putBoolean("check", true);
            }

            addOrUpdateAdapter();
            displayResult();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleContent(int responseCode) {
        resetScrollFlag();
        if(responseCode == 1) {
            Log.i("News Content", "Handle Response");
            displayResult();
        } else if(responseCode == 0) {
            Log.i("News Content", "Handle Error");

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"Check your internet connection",Toast.LENGTH_SHORT).show();
                }

            });

        }
    }
}
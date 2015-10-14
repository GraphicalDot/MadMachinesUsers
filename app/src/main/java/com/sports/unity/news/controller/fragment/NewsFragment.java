package com.sports.unity.news.controller.fragment;

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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Edwin on 15/02/2015.
 */
public class NewsFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    static int skip = 0;
    static String url = "http://52.74.250.156:8080/football?skip=" + String.valueOf(skip) + "&limit=10&image_size=hdpi";
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<String> summary;
    ArrayList<String> title;
    ArrayList<String> image_url;
    ArrayList<Long> published;
    ArrayList<String> newsLink;
    ArrayList<String> website;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private TinyDB tinyDB = TinyDB.getInstance(getActivity());

    private static AsyncHttpClient client;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(com.sports.unity.R.layout.news, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2C84CC"));
        mRecyclerView = (RecyclerView) v.findViewById(com.sports.unity.R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        summary = new ArrayList<>();
        title = new ArrayList<>();
        image_url = new ArrayList<>();
        published = new ArrayList<>();
        newsLink = new ArrayList<>();
        website = new ArrayList<>();

        getData(url);


        if (mSwipeRefreshLayout != null) {

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Toast.makeText(getActivity(), "Refreshing!", Toast.LENGTH_SHORT).show();
                    //requestDataRefresh();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getData(url);
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

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.i("Yaeye!", "end called");

                    onLoadMore();

                    loading = true;
                }
            }
        });

        return v;
    }

    private void onLoadMore() {
        //loadLimit = loadLimit + 10;
        skip += 10;
        Log.i("sportunity", "Skip Limit " + skip);
        url = "http://52.74.250.156:8080/football?skip=" + skip + "&limit=10&image_size=hdpi";
        getData(url);
        mAdapter.notifyDataSetChanged();

    }


    public void getData(String url) {
        client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        if (responseObject.getString("summary") != null) {
                            summary.add(responseObject.getString("summary"));
                            title.add(responseObject.getString("title"));
                            if (responseObject.getString("hdpi") != null && !responseObject.getString("hdpi").isEmpty())
                                image_url.add(responseObject.getString("hdpi"));
                            else
                                image_url.add("null");
                            published.add(responseObject.getLong("publish_epoch"));
                            newsLink.add(responseObject.getString("news_link"));
                            website.add(responseObject.getString("website"));
                            Log.i("Data", "downloaded");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Dataaaaa", (String.valueOf(summary.size())));
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if ((tinyDB.getBoolean("check", false))) {
                            if (mAdapter == null) {
                                mAdapter = new NewsMinicardAdapter(summary, title, image_url, published, newsLink, getActivity().getApplicationContext(), getActivity());
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                if (mAdapter instanceof NewsMinicardAdapter) {
                                    Log.i("sportunity", "no change in mini adapter");
//                                mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    Log.i("sportunity", "creating mini adapter");
                                    mAdapter = new NewsMinicardAdapter(summary, title, image_url, published, newsLink, getActivity().getApplicationContext(), getActivity());
                                    mRecyclerView.setAdapter(mAdapter);

                                }
                            }
                            mAdapter.notifyDataSetChanged();

                        } else {
                            if (mAdapter == null) {
                                Log.i("sportunity", "creating news adapter");
                                mAdapter = new NewsAdapter(summary, title, image_url, published, newsLink, website, getActivity().getApplicationContext(), getActivity());
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                if (mAdapter instanceof NewsAdapter) {
                                    Log.i("sportunity", "no change in news adapter");
//                                mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    Log.i("sportunity", "creating news adapter");
                                    mAdapter = new NewsAdapter(summary, title, image_url, published, newsLink, website, getActivity().getApplicationContext(), getActivity());
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });


            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_news_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        if ((tinyDB.getBoolean("check", false)))
            menu.findItem(R.id.mini_cards).setChecked(true);
        else
            menu.findItem(R.id.mini_cards).setChecked(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.sports.unity.R.id.action_settings) {
            return true;
        }

        if (id == R.id.mini_cards) {
            if (item.isChecked()) {
                item.setChecked(false);
                tinyDB.putBoolean("check", false);
                mAdapter = new NewsAdapter(summary, title, image_url, published, newsLink, website, getActivity().getApplicationContext(), getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                item.setChecked(true);
                tinyDB.putBoolean("check", true);
                mAdapter = new NewsMinicardAdapter(summary, title, image_url, published, newsLink, getActivity().getApplicationContext(), getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
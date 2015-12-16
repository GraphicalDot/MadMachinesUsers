package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sports.unity.news.model.News;

import java.util.ArrayList;

/**
 * Created by madmachines on 11/12/15.
 */
public class BaseNewsAdapter extends RecyclerView.Adapter {

    protected ArrayList<News> news = null;
    protected Activity activity;

    public BaseNewsAdapter(ArrayList<News> news, Activity activity) {
        this.activity = activity;
        this.news = news;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public ArrayList<News> getNews() {
        return news;
    }

}

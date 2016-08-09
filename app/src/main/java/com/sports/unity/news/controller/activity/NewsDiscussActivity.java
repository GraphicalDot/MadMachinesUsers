package com.sports.unity.news.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;

public class NewsDiscussActivity extends AppCompatActivity {

    String path = "http://feeds.images.s3.amazonaws.com/2d6430c34be2a36fe16476367d3b2766_hdpi.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_discuss);

        initToolbar();
        initViews();

    }

    private void initViews() {
        ImageView slantView = (ImageView) findViewById(R.id.slant_view);
        Glide.with(this).load(path).placeholder(R.drawable.ic_user_big).dontAnimate().into(slantView);

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

}


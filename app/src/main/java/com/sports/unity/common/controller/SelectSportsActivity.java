package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.SportsGridViewAdapter;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

public class SelectSportsActivity extends AppCompatActivity {

    private int[] flag = {0, 0, 0, 0, 0};

    private ArrayList<String> sports = new ArrayList<String>();

    private Integer[] mThumbIds = {
            R.drawable.btn_basketball_disabled,
            R.drawable.btn_cricket_disabled,
            R.drawable.btn_football_disabled,
            R.drawable.btn_tennis_disabled,
            R.drawable.btn_f1_disabled,
    };
    private Integer[] mThumbIdsSelected = {
            R.drawable.btn_basketball_selected,
            R.drawable.btn_cricket_selected,
            R.drawable.btn_football_selected,
            R.drawable.btn_tennis_selected,
            R.drawable.btn_f1_selected,
    };
    private String[] mSports = {
            Constants.GAME_KEY_BASKETBALL,
            Constants.GAME_KEY_CRICKET,
            Constants.GAME_KEY_FOOTBALL,
            Constants.GAME_KEY_TENNIS,
            Constants.GAME_KEY_F1,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sports);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

//        int size = (int) getResources().getDimension(R.dimen.select_sports_page);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.select_your_favourite_sports);
        mTitle.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new SportsGridViewAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag[position] == 0) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageResource(mThumbIdsSelected[position]);
                    sports.add(mSports[position]);
                    flag[position] = 1;
                } else {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageResource(mThumbIds[position]);
                    sports.remove(mSports[position]);
                    flag[position] = 0;
                }
            }
        });

        Button next = (Button) findViewById(R.id.toLeagueSelect);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (sports.isEmpty()) {
                    Toast.makeText(SelectSportsActivity.this, R.string.select_atleast_one_sport_message, Toast.LENGTH_SHORT).show();
                } else {
                    moveOn();
                }
            }
        });

    }

    private void moveOn() {
        UserUtil.setSportsSelected(SelectSportsActivity.this, sports);

        Intent intent = new Intent(SelectSportsActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

}


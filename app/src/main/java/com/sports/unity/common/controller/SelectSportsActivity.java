package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.SportsGridViewAdapter;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.LocManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SelectSportsActivity extends AppCompatActivity {

    private LocManager locManager;
    private ArrayList<String> sports = new ArrayList<String>();

    private Thread sendInterestsThread = null;
    private String base_url = "http://54.169.217.88/set_user_interests?username=";
    private String urlToRequest = "";

    /*For future use: to add all the sports
     in sports selection screen*/
    /*private Integer[] mThumbIds = {
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
    };*/

    private Integer[] mThumbIds = {
            R.drawable.btn_cricket_disabled,
            R.drawable.btn_football_disabled
    };
    private Integer[] mThumbIdsSelected = {
            R.drawable.btn_cricket_selected,
            R.drawable.btn_football_selected
    };
    private String[] mSports = {
            Constants.GAME_KEY_CRICKET,
            Constants.GAME_KEY_FOOTBALL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_sports);
        sports = UserUtil.getSportsSelected();
        initView();

        locManager = LocManager.getInstance(this);
        locManager.buildApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        locManager.connect();
    }

    private void initView() {

        /*
         * initialise tool bar
         */
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mTitle.setText(R.string.select_your_favourite_sports);
            mTitle.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new SportsGridViewAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!sports.contains(mSports[position])) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageResource(mThumbIdsSelected[position]);
                    sports.add(mSports[position]);
                } else {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageResource(mThumbIds[position]);
                    sports.remove(mSports[position]);
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
        if (sendInterestsThread != null && sendInterestsThread.isAlive()) {

        } else {
            sendInterestsThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sendInterests();
                }
            });
            sendInterestsThread.start();
        }
        UserUtil.setSportsSelected(SelectSportsActivity.this, sports);
        Intent intent = new Intent(SelectSportsActivity.this, AdvancedFilterActivity.class);
        startActivity(intent);

        finish();
    }

    private void sendInterests() {
        urlToRequest = "";
        HttpURLConnection httpURLConnection = null;
        String interests = "";
        if (sports.size() > 1) {
            for (String sport : sports) {
                interests += "&interests=" + sport.toLowerCase();
            }
        } else {
            interests += "&interests=" + sports.get(0).toLowerCase();
        }
        urlToRequest = base_url + XMPPClient.getConnection().getUser() + interests;
        try {
            URL sendInterests = new URL(urlToRequest);
            httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setDoInput(false);
            httpURLConnection.setRequestMethod("GET");

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i("Interest Sent", " true ");
            } else {
                Log.i("Interest Sent", " false ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
            }
        }
    }

}


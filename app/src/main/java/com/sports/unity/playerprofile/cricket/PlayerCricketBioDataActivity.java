package com.sports.unity.playerprofile.cricket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.view.CustomViewPager;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerCricketBioDataActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER_KEY = "PLAYER_PROFILE_SCREEN_LISTENER";
    private static final String PLAYER_PROFILE_REQUEST_TAG = "CRICKET_PLAYER_BIO_TAG";

    public static Intent createIntent(Context context, String playerId, String playerName) {
        Intent intent = new Intent(context, PlayerCricketBioDataActivity.class);
        intent.putExtra(Constants.INTENT_KEY_ID, playerId);
        intent.putExtra(Constants.INTENT_KEY_PLAYER_NAME, playerName);
        return intent;
    }

    private String playerNameKey;
    private CircleImageView playerProfileImage;
    private TextView playerName;
    private TextView playerNationName;
    private CustomViewPager mViewPager;
    private CricketPlayerProfileAdapter cricketPlayerProfileAdapter;
    private ScrollView scrollView;
    private ProgressBar progressBar;

    ScoresContentHandler.ContentListener contentListener = new ScoresContentHandler.ContentListener() {
        @Override
        public void handleContent(String tag, String content, int responseCode) {
            hideProgress();
            if (responseCode == 200) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    initView(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "data fetching failed, check your internet connection", Toast.LENGTH_SHORT).show();
                initView(null);
            }
        }
    };

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper( REQUEST_LISTENER_KEY, null);
//        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper( REQUEST_LISTENER_KEY, new NewsDetailComponentListener(progressBar, null));
        return volleyCallComponentHelper;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScoresContentHandler.getInstance().removeResponseListener(REQUEST_LISTENER_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_cricket_bio_data);
        setToolbar();
        getIntentExtras();
        initUI();
        requestData();
    }

    private void initUI() {
        playerProfileImage = (CircleImageView) findViewById(R.id.iv_cricket_player_profile_image);
        playerName = (TextView) findViewById(R.id.tv_player_name);
        playerNationName = (TextView) findViewById(R.id.tv_player_nation_name);
        mViewPager = (CustomViewPager) findViewById(R.id.cricket_player_pager);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ImageView img = (ImageView) findViewById(R.id.back_img);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getStringExtra(Constants.INTENT_KEY_PLAYER_NAME) != null) {
            playerName.setText(getIntent().getStringExtra(Constants.INTENT_KEY_PLAYER_NAME));
        }
    }

    private void requestData() {
        showProgress();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PLAYER_NAME, playerNameKey);
        parameters.put(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_CRICKET);
        ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, REQUEST_LISTENER_KEY, PLAYER_PROFILE_REQUEST_TAG);
    }

    private void showProgress() {
        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void getIntentExtras() {
        playerNameKey = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
    }

    private void initView(JSONObject jsonObject) {

        String cricketMatchPlayer[] = {getString(R.string.PLAYER_BIO), getString(R.string.PLAYER_STATS)};
        int numberOfplayerProfileTabs = cricketMatchPlayer.length;
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setTabTextColor(R.color.filter_tab_selector);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.app_theme_blue);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout

        cricketPlayerProfileAdapter = new CricketPlayerProfileAdapter(getSupportFragmentManager(), cricketMatchPlayer, numberOfplayerProfileTabs, jsonObject);
        mViewPager.setAdapter(cricketPlayerProfileAdapter);
        tabs.setViewPager(mViewPager);

//        int tab_index = getIntent().getIntExtra("tab_index", 0);
//        mViewPager.setCurrentItem(tab_index);
        setProfileInfo(jsonObject);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView back = (ImageView) toolbar.findViewById(R.id.back_img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setProfileInfo(JSONObject object) {
        if (object != null) {
            try {
                JSONArray jsonArray = object.getJSONArray("data");
                JSONObject dataObject = jsonArray.getJSONObject(0);

                if (dataObject.getString("player_image") != null) {
                    Glide.with(PlayerCricketBioDataActivity.this).load(dataObject.getString("player_image")).placeholder(R.drawable.ic_user).dontAnimate().into(playerProfileImage);
                }

                JSONObject playerInfo = dataObject.getJSONObject("info");
                playerName.setText(playerInfo.getString("full_name"));
                playerNationName.setText(dataObject.getString("team"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //do nothing
        }
    }
}

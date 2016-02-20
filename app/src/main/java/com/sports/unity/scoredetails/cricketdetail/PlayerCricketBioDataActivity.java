package com.sports.unity.scoredetails.cricketdetail;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.controller.CricketPlayerProfileAdapter;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerCricketBioDataActivity extends CustomVolleyCallerActivity {
    private String playerNameKey;
     private CircleImageView playerProfileImage;
    private TextView playerName;
    private TextView playerNationName;
    private ViewPager mViewPager;
    private CricketPlayerProfileAdapter cricketPlayerProfileAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_player_cricket_bio_data);
            Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            getIntentExtras();
            initView();
            setInitData();
            setToolbar();

        }catch (Exception e) {
            Log.i("playerProfile","Player is not Exists");
            e.printStackTrace();
        }
    }
    private void getIntentExtras() {
        //playerNameKey = getIntent().getStringExtra("name");
        playerNameKey="6f65e8cd45ae14c916cf2c1c69b6102c";

    }
    private void initView() {
        try{
            playerProfileImage = (CircleImageView) findViewById(R.id.iv_cricket_player_profile_image);
            playerName = (TextView) findViewById(R.id.tv_player_name);
            playerNationName = (TextView) findViewById(R.id.tv_player_nation_name);
            mViewPager = (ViewPager) findViewById(R.id.cricket_player_pager);
            String cricketMatchPlayer[] = {getString(R.string.PLAYER_BIO), getString(R.string.PLAYER_STATS)};
            int numberOfplayerProfileTabs = cricketMatchPlayer.length;
            SlidingTabLayout tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.app_theme_blue);
                }
            });
            // Setting the ViewPager For the SlidingTabsLayout


            cricketPlayerProfileAdapter = new CricketPlayerProfileAdapter(getSupportFragmentManager(),cricketMatchPlayer,numberOfplayerProfileTabs);
            mViewPager.setAdapter(cricketPlayerProfileAdapter);
            tabs.setViewPager(mViewPager);
            int tab_index = getIntent().getIntExtra("tab_index", 0);
            mViewPager.setCurrentItem(tab_index);
           /* ImageView img = (ImageView) findViewById(R.id.back_img);
            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        /*ImageView back = (ImageView) toolbar.findViewById(R.id.backarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }
    private void setInitData() {
        try {
           HashMap<String, String> parameters = new HashMap<>();
            parameters.put(Constants.PLAYER_NAME, playerNameKey);
           /* ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, REQUEST_LISTENER_KEY, PLAYER_PROFILE_REQUEST_TAG);*/
      }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setProfileInfo(final JSONObject object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    final JSONObject playerInfo = (JSONObject) object.get("info");
                    if (object != null) {
                        if (!object.isNull("image")) {
                            Log.i("run: ",object.getString("image"));
                            Glide.with(PlayerCricketBioDataActivity.this).load(object.getString("image")).placeholder(R.drawable.ic_no_img).into(playerProfileImage);
                        }
                        if (!playerInfo.isNull("Full Name")) {
                            playerName.setText(playerInfo.getString("Full Name"));
                        }
                        if (!playerInfo.isNull("Place of birth")) {
                            playerNationName.setText(playerInfo.getString("Place of birth").toUpperCase());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

package com.sports.unity.scoredetails.cricketdetail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.CricketPlayerProfileAdapter;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerCricketBioDataActivity extends CustomVolleyCallerActivity {
    private String playerNameKey;
    private static final String REQUEST_LISTENER_KEY = "PLAYER_PROFILE_SCREEN_LISTENER";
    private static final String PLAYER_PROFILE_REQUEST_TAG = "playerProfileTag";
    private CircleImageView playerProfileImage;
    private TextView playerName;
    private TextView playerNationName;
    private TextView tvPlayerDateOfPlace;
    private TextView tvPlayerDateOfBirth;
    private TextView tvPlayerbattingStyle;
    private TextView tvPlayerBowingStyle;
    private TextView tvPlayerMajorTeam;
    private ImageView ivDown;
    private ImageView ivDownSecond;
    private GridLayout battingGridLayout;
    private GridLayout bowlingGridLayout;
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
            {
                //ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
               // PlayerProfileComponentListener playerProfileComponentListener = new PlayerProfileComponentListener(progressBar);
               /* ArrayList<CustomComponentListener> listeners = new ArrayList<>();
                listeners.add(playerProfileComponentListener);
                onComponentCreate(listeners, REQUEST_LISTENER_KEY);*/
            }
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
            /*playerProfileImage = (CircleImageView) findViewById(R.id.cricket_player_profile_image);
            playerName = (TextView) findViewById(R.id.player_name);
            playerNationName = (TextView) findViewById(R.id.tv_player_nation_name);
            tvPlayerDateOfPlace = (TextView) findViewById(R.id.tv_player_date_of_place);
            tvPlayerDateOfBirth = (TextView) findViewById(R.id.tv_player_date_of_birth);
            tvPlayerbattingStyle = (TextView) findViewById(R.id.tv_player_batting_style);
            tvPlayerBowingStyle = (TextView) findViewById(R.id.tv_player_bowing_style);
            tvPlayerMajorTeam = (TextView) findViewById(R.id.tv_player_major_team);
            ivDown = (ImageView) findViewById(R.id.iv_down);
            ivDownSecond = (ImageView) findViewById(R.id.iv_down_second);
            battingGridLayout = (GridLayout) findViewById(R.id.gl_batting_performance_summery);
            bowlingGridLayout = (GridLayout) findViewById(R.id.gl_bowling_performance_summery);*/
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
           /* HashMap<String, String> parameters = new HashMap<>();
            parameters.put(Constants.PLAYER_NAME, playerNameKey);
            ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, REQUEST_LISTENER_KEY, PLAYER_PROFILE_REQUEST_TAG);
     */   }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*private class PlayerProfileComponentListener extends CustomComponentListener {

        private boolean success;

        public PlayerProfileComponentListener(ProgressBar progressBar) {
            super(PLAYER_PROFILE_REQUEST_TAG, progressBar, null);
        }

        @Override
        protected void showErrorLayout() {
            Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            try {
                JSONObject response = new JSONObject(content);
                if (response.getString("status").equals("200")) {
                    this.success = true;
                    Log.i("player profile", content);
                    populateData(content);

                } else {
                    this.success = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            success = true;
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI() {

        }


    }*/
    private void populateData(String content){
        try {

            JSONObject jsonObject = new JSONObject(content);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if( success ) {
                JSONArray array = (JSONArray) jsonObject.get("data");
                for( int index=0; index < array.length(); index++){
                    JSONObject object = array.getJSONObject(index);
                    if(object == null){
                        continue;
                    } else {
                        if(!object.isNull("name")){
                            playerName.setText(object.getString("name"));

                        }
                        if(!object.isNull("name")){
                            playerNationName.setText(object.getString("name"));
                        }
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.player_details_not_exists, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
        }





    }


}

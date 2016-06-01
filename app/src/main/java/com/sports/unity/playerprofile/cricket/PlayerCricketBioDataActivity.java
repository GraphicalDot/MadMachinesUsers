package com.sports.unity.playerprofile.cricket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.view.CustomViewPager;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private JSONObject playerProfileBio;

    private ScrollView scrollView;
    private CircleImageView playerProfileImage;
    private TextView playerName;
    private TextView playerNationName;

    private CustomViewPager mViewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private ProgressBar progressBar;
    private ViewGroup errorLayout = null;
    private boolean isPlayerFav = false;
    private ArrayList<FavouriteItem> favList;
    private ImageView isFav;
    private boolean isResultRequired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_cricket_bio_data);
        favList = FavouriteItemWrapper.getInstance(getApplicationContext()).getFavList();
        getIntentExtras();

        setToolbar();
        initUI();

        {
            onComponentCreate();
            requestPlayerProfile();
        }
    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper(REQUEST_LISTENER_KEY, new PlayerCricketBioComponentListener(progressBar, errorLayout));
        return volleyCallComponentHelper;
    }

    @Override
    public void onBackPressed() {
        if (isResultRequired) {
            setResult(RESULT_OK, getIntent());
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getIntentExtras() {
        playerNameKey = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        isResultRequired = getIntent().getBooleanExtra(Constants.RESULT_REQUIRED, false);
    }

    private void initUI() {
        playerProfileImage = (CircleImageView) findViewById(R.id.iv_cricket_player_profile_image);
        playerName = (TextView) findViewById(R.id.tv_player_name);
        playerNationName = (TextView) findViewById(R.id.tv_player_nation_name);
        mViewPager = (CustomViewPager) findViewById(R.id.cricket_player_pager);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorLayout = (ViewGroup) findViewById(R.id.error);

        ImageView img = (ImageView) findViewById(R.id.back_img);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isResultRequired) {
                    setResult(RESULT_OK, getIntent());
                }
                finish();
            }
        });

        if (getIntent().getStringExtra(Constants.INTENT_KEY_PLAYER_NAME) != null) {
            playerName.setText(getIntent().getStringExtra(Constants.INTENT_KEY_PLAYER_NAME));
        }
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

        setUpFavoriateIcon(toolbar);
    }

    private void setUpFavoriateIcon(Toolbar toolbar) {

        isFav = (ImageView) toolbar.findViewById(R.id.favoriate);
        ArrayList<FavouriteItem> players = FavouriteItemWrapper.getInstance(getApplicationContext()).getCricketPlayers();
        isFav.setImageResource(R.drawable.ic_non_fav);
        for (FavouriteItem item : players) {
            if (item.getId().equals(playerNameKey)) {
                isPlayerFav = true;
                isFav.setImageResource(R.drawable.ic_fav);
                break;
            }
        }
        isFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayerFav) {
                    isFav.setImageResource(R.drawable.ic_non_fav);
                    removeFromFavList();
                    isPlayerFav = false;
                } else {
                    isFav.setImageResource(R.drawable.ic_fav);
                    addToFavList();
                    isPlayerFav = true;
                }
                FavouriteItemWrapper.getInstance(getApplicationContext()).saveList(getApplicationContext(), favList);
            }
        });
    }

    private void removeFromFavList() {
        int position = 0;
        for (int i = 0; i < favList.size(); i++) {
            if (favList.get(i).getId().equals(playerNameKey)) {
                position = i;
                break;
            }
        }
        favList.remove(position);
    }

    private void addToFavList() {
        FavouriteItem item = new FavouriteItem();
        item.setId(playerNameKey);
        item.setName(playerName.getText().toString());
        item.setSportsType(Constants.SPORTS_TYPE_CRICKET);
        item.setFilterType(Constants.FILTER_TYPE_PLAYER);
        favList.add(item);
    }

    private boolean renderResponse(JSONObject jsonObject) {
        boolean success = false;
        if (playerProfileBio != null) {
            try {
                SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
                tabs.setDistributeEvenly(true);

                tabs.setTabTextColor(R.color.filter_tab_selector);
                tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                    @Override
                    public int getIndicatorColor(int position) {
                        return getResources().getColor(R.color.app_theme_blue);
                    }
                });

                viewPagerAdapter = new ViewPagerAdapter();
                mViewPager.setAdapter(viewPagerAdapter);
                tabs.setViewPager(mViewPager);

                renderUserBasicInfo();
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //nothing
        }

        if (success) {
            scrollView.setVisibility(View.VISIBLE);
        }
        return success;
    }

    private void requestPlayerProfile() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PLAYER_NAME, playerNameKey);
        parameters.put(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_CRICKET);
        requestContent(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, PLAYER_PROFILE_REQUEST_TAG);
    }

    private boolean handleResponse(String response) {
        boolean success = false;
        try {
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.getBoolean("success")) {
                playerProfileBio = responseJson;
                success = true;
            } else {
                success = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private void renderUserBasicInfo() throws Exception {
        JSONArray jsonArray = playerProfileBio.getJSONArray("data");
        JSONObject dataObject = jsonArray.getJSONObject(0);

        if (dataObject.getString("player_image") != null) {
            Glide.with(PlayerCricketBioDataActivity.this).load(dataObject.getString("player_image")).placeholder(R.drawable.ic_user).dontAnimate().into(playerProfileImage);
        }

        JSONObject playerInfo = dataObject.getJSONObject("info");
        playerName.setText(playerInfo.getString("full_name"));
        playerNationName.setText(dataObject.getString("team"));
    }

    private void renderPlayerBio(ViewGroup viewGroup) {
        LinearLayout linearLayoutBio = (LinearLayout) viewGroup.findViewById(R.id.ll_bio_layout);
        linearLayoutBio.setVisibility(View.VISIBLE);

        ViewGroup errorLayout = (ViewGroup) viewGroup.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

        TextView tvPlayerDateOfBirth = (TextView) viewGroup.findViewById(R.id.tv_player_date_of_birth);
        TextView tvPlayerbattingStyle = (TextView) viewGroup.findViewById(R.id.tv_player_batting_style);
        TextView tvPlayerBowingStyle = (TextView) viewGroup.findViewById(R.id.tv_player_bowing_style);
        TextView tvPlayerMajorTeam = (TextView) viewGroup.findViewById(R.id.tv_player_major_team);
        TextView tvPlayerbirthOfPlace = (TextView) viewGroup.findViewById(R.id.tv_player_birth_of_place);

        boolean success = false;
        try {
            JSONArray dataArray = playerProfileBio.getJSONArray("data");
            JSONObject dataObject = dataArray.getJSONObject(0);
            JSONObject playerInfo = dataObject.getJSONObject("info");

            tvPlayerDateOfBirth.setText(DateUtil.getFormattedDateDDMMYYYY(playerInfo.getString("born")));
            tvPlayerbattingStyle.setText(playerInfo.getString("batting_style"));
            tvPlayerBowingStyle.setText(playerInfo.getString("bowling_style"));
            tvPlayerbirthOfPlace.setText(playerInfo.getString("birth_place"));
            tvPlayerMajorTeam.setText(dataObject.getString("team"));

            success = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (success) {
            //nothing
        } else {
            linearLayoutBio.setVisibility(View.GONE);

            errorLayout.setVisibility(View.VISIBLE);
            CustomComponentListener.renderAppropriateErrorLayout(errorLayout);
        }
    }

    private void renderPlayerStats(ViewGroup viewGroup) {
        CricketPlayerMatchStat cricketPlayerMatchStat = new CricketPlayerMatchStat();
        cricketPlayerMatchStat.populateData(viewGroup, playerProfileBio);
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private String titles[] = {getString(R.string.PLAYER_BIO), getString(R.string.PLAYER_STATS)};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int resId = 0;
            ViewGroup viewgroup = null;
            switch (position) {
                case 0:
                    resId = R.layout.fragment_player_cricket_bio;
                    viewgroup = (ViewGroup) inflater.inflate(resId, collection, false);
                    collection.addView(viewgroup);

                    renderPlayerBio(viewgroup);
                    break;
                case 1:
                    resId = R.layout.fragment_players_cricket_stat_batting;
                    viewgroup = (ViewGroup) inflater.inflate(resId, collection, false);
                    collection.addView(viewgroup);

                    renderPlayerStats(viewgroup);
                    break;
            }
            return viewgroup;
        }

    }

    private class PlayerCricketBioComponentListener extends CustomComponentListener {

        public PlayerCricketBioComponentListener(ProgressBar progressBar, ViewGroup errorLayout) {
            super(PLAYER_PROFILE_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public void handleErrorContent(String tag) {
            //nothing
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return PlayerCricketBioDataActivity.this.handleResponse(content);
        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderResponse(playerProfileBio);
            if (!success) {
                showErrorLayout();
            } else {
                isFav.setVisibility(View.VISIBLE);
            }
        }

    }

}
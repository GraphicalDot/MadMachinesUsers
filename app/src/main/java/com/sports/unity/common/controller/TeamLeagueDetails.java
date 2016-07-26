package com.sports.unity.common.controller;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

public class TeamLeagueDetails extends CustomAppCompatActivity {

    private String id;
    private String name;
    private String type;
    private FavouriteItem favouriteItem;
    private boolean isStaffPicked;
    private ArrayList<FavouriteItem> favList;
    private boolean isTeamLeagueFav = false;
    private boolean isResultRequired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_league_details);
        favList = FavouriteItemWrapper.getInstance(getApplicationContext()).getFavList();
        String jsonObject = getIntent().getStringExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA);
        isStaffPicked = getIntent().getExtras().getBoolean(Constants.SPORTS_TYPE_STAFF, false);
        favouriteItem = new FavouriteItem(jsonObject);
        name = favouriteItem.getName();
        id = favouriteItem.getId();
        type = favouriteItem.getSportsType();
        isResultRequired = getIntent().getBooleanExtra(Constants.RESULT_REQUIRED, false);
        initToolbar();
        initView();

    }

    @Override
    public void onBackPressed() {
        if (isResultRequired) {
            setResult(RESULT_OK, getIntent());
        }
        finish();
    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_filter);
        title.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());
        title.setText(name);


        TextView next = (TextView) toolbar.findViewById(R.id.toolbar_title);
        next.setVisibility(View.GONE);

        ImageView back = (ImageView) toolbar.findViewById(R.id.cancel);
        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isResultRequired) {
                    setResult(RESULT_OK, getIntent());
                }
                finish();
            }
        });

        setUpFavoriateOnToolbar(toolbar);

    }

    private void setUpFavoriateOnToolbar(Toolbar toolbar) {
        if (isStaffPicked) {
            //nothing
        } else {
            final ImageView isFav = (ImageView) toolbar.findViewById(R.id.favoriate);
            isFav.setVisibility(View.VISIBLE);
            for (FavouriteItem item : favList) {
                if (favouriteItem.getId().equals(item.getId())) {
                    isTeamLeagueFav = true;
                    isFav.setImageResource(R.drawable.ic_fav);
                    break;
                }
            }
            isFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTeamLeagueFav) {
                        isFav.setImageResource(R.drawable.ic_non_fav);
                        isTeamLeagueFav = false;
                        removeFromFavList();
                    } else {
                        isFav.setImageResource(R.drawable.ic_fav);
                        isTeamLeagueFav = true;
                        addToFavList();
                    }
                    FavouriteItemWrapper.getInstance(getApplicationContext()).saveList(getApplicationContext(), favList);
                }
            });
        }
    }

    private void removeFromFavList() {
        int position = 0;
        for (int i = 0; i < favList.size(); i++) {
            if (favList.get(i).getId().equals(favouriteItem.getId())) {
                position = i;
                break;
            }
        }
        favList.remove(position);
    }

    private void addToFavList() {
        FavouriteItem item = new FavouriteItem();
        item.setId(favouriteItem.getId());
        item.setName(favouriteItem.getName());
        item.setSportsType(favouriteItem.getSportsType());
        item.setFilterType(favouriteItem.getFilterType());
        favList.add(item);
    }

    private void initView() {

        // String titles[] = {getString(R.string.fixture), getString(R.string.news)};
        // int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        ViewPagerAdapterForTeamAndLeagueDetails adapter = new ViewPagerAdapterForTeamAndLeagueDetails(getSupportFragmentManager(), favouriteItem, isStaffPicked);

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(com.sports.unity.R.id.pager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setTabTextColor(R.color.filter_tab_selector);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.app_theme_blue);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }
}

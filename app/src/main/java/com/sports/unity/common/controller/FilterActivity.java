package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.Constants;


import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private int[] sportsCategoryLayoutID = new int[]{R.id.cricket, R.id.football};
    private boolean[] checkedFlag = new boolean[]{false, false};
    private LinearLayout teamFilter, leagueFilter, playerFilter;
    private ArrayList<String> sportsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);
        setToolBar();
        sportsSelected = UserUtil.getSportsSelected();
        initCheckedFlagList();
        initViews();
        setTab();
    }

    private void initCheckedFlagList() {
        ArrayList<String> filter = UserUtil.getSportsSelected();
        if (filter.contains(Constants.GAME_KEY_CRICKET)) {
            checkedFlag[0] = true;
        }
        if (filter.contains(Constants.GAME_KEY_FOOTBALL)) {
            checkedFlag[1] = true;
        }
    }

    private void saveFilterlist() {
        ArrayList<String> filter = new ArrayList<>();

        if (checkedFlag[0]) {
            filter.add(Constants.GAME_KEY_CRICKET);
        }
        if (checkedFlag[1]) {
            filter.add(Constants.GAME_KEY_FOOTBALL);
        }

        UserUtil.setSportsSelected(this, filter);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveOn(false);
            }
        });
        TextView titleFilter = (TextView) toolbar.findViewById(R.id.toolbar_filter);
        titleFilter.setTypeface(FontTypeface.getInstance(this).getRobotoSlabRegular());
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setTypeface(FontTypeface.getInstance(this).getRobotoSlabRegular());

        title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveOn(true);
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initViews() {
        TextView editSports = (TextView) findViewById(R.id.edit);
        editSports.setTypeface(FontTypeface.getInstance(this).getRobotoSlabRegular());

        TextView filterBySports = (TextView) findViewById(R.id.filter);
        filterBySports.setTypeface(FontTypeface.getInstance(this).getRobotoSlabRegular());

        TextView advanceFilter = (TextView) findViewById(R.id.filter3);
        advanceFilter.setTypeface(FontTypeface.getInstance(this).getRobotoSlabRegular());

        for (int loop = 0; loop < sportsCategoryLayoutID.length; loop++) {
            initCheckBox(sportsCategoryLayoutID[loop], checkedFlag[loop], loop);
        }

        editSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, SelectSportsActivity.class);
                intent.putExtra(Constants.IS_FROM_NAV, true);
                startActivity(intent);
            }
        });


//        teamFilter = (LinearLayout) findViewById(R.id.adv1);
//        leagueFilter = (LinearLayout) findViewById(R.id.adv2);
//        playerFilter = (LinearLayout) findViewById(R.id.adv3);
//
//        teamFilter.setOnClickListener(this);
//        leagueFilter.setOnClickListener(this);
//        playerFilter.setOnClickListener(this);

    }

    private void setTab() {

        String titles[] = {"Teams", "Leagues", "Players"};

        int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        ViewPagerAdapterForFilter adapter = new ViewPagerAdapterForFilter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(R.id.filter_activity_pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
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

    private void initCheckBox(int layoutId, boolean checked, int index) {
        LinearLayout layout = (LinearLayout) findViewById(layoutId);
        layout.setTag(index);

        initTextViewBasedOnCheckFlag(layout, checked);

        CheckBox checkbox = (CheckBox) layout.getChildAt(2);
        checkbox.setChecked(checked);
    }

    private void initTextViewBasedOnCheckFlag(LinearLayout layout, boolean checked) {
        TextView title = (TextView) layout.getChildAt(1);
        if (checked) {
            title.setTextColor(getResources().getColor(R.color.app_theme_blue));
        } else {
            title.setTextColor(getResources().getColor(R.color.gray1));
        }
    }

    private int checkedItemCount() {
        int count = 0;
        for (int loop = 0; loop < sportsCategoryLayoutID.length; loop++) {
            if (checkedFlag[loop] == true) {
                count++;
            }
        }
        return count;
    }

    private void onClickCheckBox(int layoutId) {
        LinearLayout layout = (LinearLayout) findViewById(layoutId);
        int index = (Integer) layout.getTag();

        boolean checked = checkedFlag[index];

        if (checked && checkedItemCount() == 1) {
            Toast.makeText(this, R.string.keep_one_sport_selected, Toast.LENGTH_SHORT).show();
        } else {
            checkedFlag[index] = !checked;

            CheckBox checkbox = (CheckBox) layout.getChildAt(2);
            checkbox.setChecked(checkedFlag[index]);

            initTextViewBasedOnCheckFlag(layout, checkedFlag[index]);
        }
    }

    private void moveOn(boolean saveFilter) {
        if (saveFilter == true) {
            saveFilterlist();
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }

    }

    public void onCheckboxClicked(View view) {
        onClickCheckBox(view.getId());
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.adv1:
//                Intent advancedFilterTeam = new Intent(this, AdvancedFilterActivity.class);
//                advancedFilterTeam.putExtra(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
//                startActivity(advancedFilterTeam);
//                break;
//            case R.id.adv2:
//                if(sportsSelected.contains(Constants.GAME_KEY_FOOTBALL)) {
//                    Intent advancedFilterLeague = new Intent(this, AdvancedFilterActivity.class);
//                    advancedFilterLeague.putExtra(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_LEAGUE);
//                    startActivity(advancedFilterLeague);
//                }else{
//                    Toast.makeText(this,"Please follow football to view leagues.",Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.adv3:
//                Intent advancedFilterPlayer = new Intent(this, AdvancedFilterActivity.class);
//                advancedFilterPlayer.putExtra(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_PLAYER);
//                startActivity(advancedFilterPlayer);
//                break;
//        }
//    }
}

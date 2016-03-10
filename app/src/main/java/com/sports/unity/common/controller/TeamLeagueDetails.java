package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.Constants;

public class TeamLeagueDetails extends CustomAppCompatActivity {

    private String id = null;
    private String name = null;
    private String type = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_league_details);

        name = getIntent().getStringExtra("Name");
        id = getIntent().getStringExtra("Id");
        type = getIntent().getStringExtra("Type");

        initToolbar();
        initView();

    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.gray3));

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_filter);
        title.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());
        title.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
        title.setText(name);

        TextView next = (TextView) toolbar.findViewById(R.id.toolbar_title);
        next.setVisibility(View.GONE);

        ImageView back = (ImageView) toolbar.findViewById(R.id.cancel);
        back.setImageResource(R.drawable.ic_menu_back_blk);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {

       // String titles[] = {getString(R.string.fixture), getString(R.string.news)};
       // int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        ViewPagerAdapterForTeamAndLeagueDetails adapter = new ViewPagerAdapterForTeamAndLeagueDetails(getSupportFragmentManager(), id, name, type);

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(com.sports.unity.R.id.pager);
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

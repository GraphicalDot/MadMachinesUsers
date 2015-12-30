package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.SlidingTabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MainActivity extends CustomAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.sports.unity.R.layout.activity_main);

        SportsUnityDBHelper.getInstance(this).addDummyMessageIfNotExist();
        XMPPService.startService(MainActivity.this);

        initViews();
        setNavigation();

    }

    private void setNavigation() {
        ViewGroup view = (ViewGroup) findViewById(R.id.navigation);
        TextView name = (TextView) view.findViewById(R.id.name);
        CircleImageView prof_img = (CircleImageView) findViewById(R.id.circleView);
        name.setText("Hello");
    }


    private void initViews() {
        Toolbar toolbar = initToolBar();

        final DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }

        });

        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        String titles[] = {getString(R.string.scores), getString(R.string.news), getString(R.string.messages)};
        int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        ViewPagerAdapterInMainActivity adapter = new ViewPagerAdapterInMainActivity(getSupportFragmentManager(), titles, numberOfTabs);

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(com.sports.unity.R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorPrimary);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        //set news pager as default
        int tab_index = getIntent().getIntExtra("tab_index", 1);
        pager.setCurrentItem(tab_index);
    }

    private Toolbar initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.app_name);
        title.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        return toolbar;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if( mDrawerLayout.isDrawerOpen( Gravity.LEFT) ){
            mDrawerLayout.closeDrawer( Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

}
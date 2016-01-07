package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.fragment.AdvancedFilterFragment;
import com.sports.unity.common.controller.fragment.NavigationFragment;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.LocManager;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MainActivity extends CustomAppCompatActivity {

    NavigationFragment navigationFragment;
    public boolean isPaused;
    
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.sports.unity.R.layout.activity_main);

        SportsUnityDBHelper.getInstance(this).addDummyMessageIfNotExist();
        XMPPService.startService(MainActivity.this);
        
        initViews();
        setNavigation();
        
        LocManager.getInstance(this).getLocation();
    }

    private void setNavigation() {
        navigationFragment = new NavigationFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, navigationFragment, "Nav_frag").commit();
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
    protected void onResume() {
        super.onResume();
        if(!isPaused) {
            navigationFragment = new NavigationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, navigationFragment, "Nav_frag").commit();
        }
        isPaused=false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        navigationFragment.onActivityResult(requestCode, resultCode, data);
        Log.d("max", "ONMAINRESULT");
    }
}
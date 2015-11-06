package com.sports.unity.common.controller;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.view.SlidingTabLayout;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.sports.unity.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.sports.unity.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        Toolbar toolbar = initToolBar();

        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        };

        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        String titles[] = {getString(R.string.scores), getString(R.string.news), getString(R.string.messages)};
        int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, numberOfTabs);

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
                return getResources().getColor(com.sports.unity.R.color.tabsScrollColor);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        //set news pager as default
        pager.setCurrentItem(1);
    }

    private Toolbar initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.app_name);
        title.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        return toolbar;
    }

//    private class CheckConnection extends AsyncTask<Void, Void, XMPPTCPConnection> {
//        boolean success;
//
//        @Override
//        protected XMPPTCPConnection doInBackground(Void... params) {
//            success = XMPPClient.reconnectConnection();
//            if (success) {
//                success = XMPPClient.authenticateConnection(MainActivity.this);
//            } else {
//                //nothing
//            }
//
//            return XMPPClient.getConnection();
//        }
//
//        @Override
//        protected void onPostExecute(XMPPTCPConnection con) {
//            //getForms(con);
//        }
//
//    }

}
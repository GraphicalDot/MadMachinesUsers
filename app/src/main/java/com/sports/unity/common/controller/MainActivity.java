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
import android.support.v7.app.AppCompatActivity;
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
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MainActivity extends AppCompatActivity {

    public static UserSearchManager searchManager;
    public static Form searchForm = null;
    public static Form answerForm = null;
    private Presence presence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.sports.unity.R.layout.activity_main);

        new CheckConnection().execute();
        SportsUnityDBHelper.getInstance(this).addDummyMessageIfNotExist();

        initViews();
        setNavigation();
        /*
         * Retain contact information
         */
        RetainDataFragment retainDataFragment = new RetainDataFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(retainDataFragment, "data");
        fragmentTransaction.commit();
    }

    private void setNavigation()
    {
        ViewGroup view=(ViewGroup) findViewById(R.id.navigation);
        TextView name=(TextView) view.findViewById(R.id.name);
        CircleImageView prof_img=(CircleImageView) findViewById(R.id.circleView);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void getForms(XMPPTCPConnection con) {
        searchManager = new UserSearchManager(con);
        try {
            searchForm = searchManager.getSearchForm("vjud.mm.io");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        answerForm = searchForm.createAnswerForm();
        if (answerForm != null) {
            new Users().execute();
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private class Users extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                ContactsHandler.getInstance().updateRegisteredUsers(MainActivity.this);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class CheckConnection extends AsyncTask<Void, Void, XMPPTCPConnection> {

        @Override
        protected XMPPTCPConnection doInBackground(Void... params) {
            if (!XMPPClient.getConnection().isAuthenticated()) {
                try {
                    TinyDB tinyDB = TinyDB.getInstance(MainActivity.this);
                    String username = tinyDB.getString(TinyDB.KEY_USERNAME);
                    String password = tinyDB.getString(TinyDB.KEY_PASSWORD);
                    XMPPClient.getConnection().login(username, password);
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return XMPPClient.getConnection();

            } else {
                return XMPPClient.getConnection();
            }
        }

        @Override
        protected void onPostExecute(XMPPTCPConnection con) {
            if (isMyServiceRunning(XMPPService.class) && XMPPClient.getConnection().isAuthenticated()) {
                Log.i("Service is :", "Running");
                presence = new Presence(Presence.Type.available);
                try {
                    con.sendPacket(presence);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                getForms(con);

            } else {
                Intent serviceIntent = new Intent(MainActivity.this, XMPPService.class);
                startService(serviceIntent);
                if (XMPPClient.getConnection().isAuthenticated()) {
                    presence = new Presence(Presence.Type.available);
                    try {
                        con.sendPacket(presence);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    getForms(con);
                }
            }
        }

    }

}
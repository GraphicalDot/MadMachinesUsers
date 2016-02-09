package com.sports.unity.common.controller;

import android.content.Intent;
import android.location.Location;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.GetChars;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.fragment.NavigationFragment;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.LocManager;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MainActivity extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    NavigationFragment navigationFragment;
    public boolean isPaused;

    private XMPPTCPConnection con;
    private SportsUnityDBHelper sportsUnityDBHelper;
    public SearchView searchView;
    LocManager locManager;
    private PermissionResultHandler contactResultHandler, locationResultHandelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FavouriteItemWrapper.getInstance().getFavList(this);
        setContentView(com.sports.unity.R.layout.activity_main);

        SportsUnityDBHelper.getInstance(this).addDummyMessageIfNotExist();
        XMPPService.startService(MainActivity.this);

        initViews();
        setNavigation(savedInstanceState);

        con = XMPPClient.getConnection();
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
        setNavigationProfile();

        locManager = LocManager.getInstance(getApplicationContext());
        locManager.buildApiClient();

    }

    public void setNavigationProfile() {

        LinearLayout navHeader = (LinearLayout) findViewById(R.id.nav_header);

        CircleImageView profile_photo = (CircleImageView) navHeader.findViewById(R.id.circleView);
        final TextView name = (TextView) navHeader.findViewById(R.id.name);

        final String user_name = TinyDB.getInstance(this).getString(TinyDB.KEY_PROFILE_NAME);
        String user_details = TinyDB.getInstance(this).getString(TinyDB.KEY_USERNAME);

        final Contacts contact = sportsUnityDBHelper.getContact(user_details);


        if (contact.image != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(contact.image, 0, contact.image.length);
            profile_photo.setImageBitmap(bmp);
        } else {
            profile_photo.setImageResource(R.drawable.ic_user);
        }


        name.setText(user_name);

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.putExtra(Constants.IS_OWN_PROFILE,true);
                intent.putExtra("name", user_name);
                intent.putExtra("profilePicture", contact.image);
                startActivity(intent);
            }
        });

    }

    private void setNavigation(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            navigationFragment = new NavigationFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.nav_fragment, navigationFragment, "Nav_frag").commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locManager.connect();
    }

    private void initViews() {
        Toolbar toolbar = initToolBar();

        final DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout navigationHeader = (LinearLayout) mDrawer.findViewById(R.id.nav_header);
        TextView name = (TextView) navigationHeader.findViewById(R.id.name);
        name.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        Switch shareLocation = (Switch) navigationHeader.findViewById(R.id.share_location);
        shareLocation.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
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
        if (!isPaused) {
            navigationFragment = new NavigationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment, navigationFragment, "Nav_frag").commit();
        }
        isPaused = false;
        updateLocation();
    }

    private void updateLocation() {
        Location location = null;
        if (locManager.ismGoogleApiClientConnected()) {
            location = locManager.getLocation();
            if (location != null) {
                locManager.sendLatituteAndLongitude(location, false);
            }
        } else {
            //TODO
        }
//        GPSTracking.getInstance(getApplicationContext()).getLocation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (searchView != null) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_NAV) {
            navigationFragment.onActivityResult(requestCode, resultCode, data);
            Log.d("max", "ONMAINRESULT");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_CONTACT_PERMISSION) {
            if (contactResultHandler != null) {
                contactResultHandler.onPermissionResult(requestCode, grantResults);
            } else {
                PermissionUtil.getInstance().showSnackBar(this, "Sorry something went wrong");
            }
        } else if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSION) {
            if (locationResultHandelar != null) {
                locationResultHandelar.onPermissionResult(requestCode, grantResults);
            } else {
                PermissionUtil.getInstance().showSnackBar(this, "Sorry something went wrong");
            }
        }

    }

    public void addContactResultListener(PermissionResultHandler permissionResultHandler) {
        this.contactResultHandler = permissionResultHandler;
    }

    public void removeContactResultListener() {
        this.contactResultHandler = null;
    }

    public void addLocationResultListener(PermissionResultHandler permissionResultHandler) {
        this.locationResultHandelar = permissionResultHandler;
    }

    public void removeLocationResultListener() {
        this.locationResultHandelar = null;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    public interface PermissionResultHandler {
        public void onPermissionResult(int requestCode, int[] grantResults);
    }

}
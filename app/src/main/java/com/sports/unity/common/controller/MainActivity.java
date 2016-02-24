package com.sports.unity.common.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.GetChars;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ActionMenuView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.fragment.NavigationFragment;
import com.sports.unity.common.model.ContactsHandler;
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

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MainActivity extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    NavigationFragment navigationFragment;

    private XMPPTCPConnection con;
    private SportsUnityDBHelper sportsUnityDBHelper;
    public SearchView searchView;
    LocManager locManager;
    private PermissionResultHandler contactResultHandler, locationResultHandelar;
    DrawerLayout drawer;
    public SlidingTabLayout tabs;
    private Toolbar toolbar;
    private TextView title;
    private ViewPager pager;
    private ViewPagerAdapterInMainActivity adapter;
    ImageView back;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FavouriteItemWrapper.getInstance(this);
        setContentView(com.sports.unity.R.layout.activity_main);

        SportsUnityDBHelper.getInstance(this).addDummyMessageIfNotExist();
        XMPPService.startService(MainActivity.this);

        initViews();
        setNavigation(savedInstanceState);

        con = XMPPClient.getConnection();
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);

        locManager = LocManager.getInstance(getApplicationContext());
        locManager.buildApiClient();

        {
            //TODO temporary snippet, will remove it.
            if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                ContactsHandler.getInstance().addCallToSyncContacts(this);
            } else {
                if (PermissionUtil.getInstance().requestPermission(this, new ArrayList<String>(Arrays.asList(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)))) {
                    ContactsHandler.getInstance().addCallToSyncContacts(this);
                }
            }
        }
    }

    public void setNavigationProfile() {

        LinearLayout navHeader = (LinearLayout) findViewById(R.id.nav_header);

        LinearLayout viewMyProfile = (LinearLayout) navHeader.findViewById(R.id.my_profile_drawer);

        TextView viewProfile = (TextView) navHeader.findViewById(R.id.view_profile);
        viewProfile.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        CircleImageView profilePhoto = (CircleImageView) navHeader.findViewById(R.id.circleView);
        final TextView name = (TextView) navHeader.findViewById(R.id.name);
        name.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        String userJid = TinyDB.getInstance(this).getString(TinyDB.KEY_USER_JID);

        final Contacts contact = sportsUnityDBHelper.getContactByJid(userJid);
        if (contact.image != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(contact.image, 0, contact.image.length);
            profilePhoto.setImageBitmap(bmp);
        } else {
            profilePhoto.setImageResource(R.drawable.ic_user);
        }

        name.setText(contact.name);

        viewMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.putExtra(Constants.IS_OWN_PROFILE, true);
                intent.putExtra("name", contact.name);
                intent.putExtra("profilePicture", contact.image);
                intent.putExtra("status", contact.status);
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
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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
                drawer.openDrawer(Gravity.LEFT);
            }

        });

        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        String titles[] = {getString(R.string.scores), getString(R.string.news), getString(R.string.messages)};
        int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapterInMainActivity(getSupportFragmentManager(), titles, numberOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(com.sports.unity.R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
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
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.app_name);
        title.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());
        back = (ImageView) toolbar.findViewById(R.id.img_back);
        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    //Intentionally written twice, please don't delete.
                    searchView.setIconified(true);
                }
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        return toolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNavigationProfile();
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

    public void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (searchView != null) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    //Intentionally written twice, please don't delete.
                    searchView.setIconified(true);
                    searchView.clearFocus();
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

    public void setSearchView(SearchView search, MenuItem item) {
        this.searchView = search;
        menuItem = item;

    }


    public void disableSearch() {
        pager.setOnTouchListener(null);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabs.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);
        findViewById(R.id.seprator).setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_theme_blue));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
    }


    public void enableSearch() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        tabs.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        findViewById(R.id.seprator).setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.textColorPrimary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        }
        pager.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });
    }

    public interface PermissionResultHandler {
        public void onPermissionResult(int requestCode, int[] grantResults);
    }


}
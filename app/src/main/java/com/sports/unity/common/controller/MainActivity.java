package com.sports.unity.common.controller;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.fragment.NavigationFragment;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.ControlledSwipeViewPager;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.messages.controller.activity.GroupDetailActivity;
import com.sports.unity.gcm.RegistrationIntentService;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.peoplearound.PeopleAroundActivity;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.LocManager;

import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MainActivity extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String projectToken = "077215613b5134abb421fd53879c42db";
    private MixpanelAPI mixpanel = null;

    NavigationFragment navigationFragment;

    private SportsUnityDBHelper sportsUnityDBHelper;
    public SearchView searchView;
    LocManager locManager;
    private PermissionResultHandler contactResultHandler, locationResultHandelar;
    DrawerLayout drawer;
    public SlidingTabLayout tabs;
    private Toolbar toolbar;
    private TextView title;
    private ControlledSwipeViewPager pager;
    private ViewPagerAdapterInMainActivity adapter;
    ImageView back;
    private boolean shouldCloseDrawer = false;
    private MenuItem menuItem;

    private ContactSyncListener contactSyncListener;
    private TextView unreadCount;
    private boolean messagesFragmentInFront = false;
    private SharedPreferences preferences;

    private FloatingActionMenu fabMenu;
    private View backgroundDimmer;
    private boolean isConnectionReplaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FavouriteItemWrapper.getInstance(this);
        setContentView(com.sports.unity.R.layout.activity_main);

        mixpanel = MixpanelAPI.getInstance(this, projectToken);
        sendMixpaneldata();

        SportsUnityDBHelper.getInstance(this).addDummyMessageIfNotExist();
        SportsUnityDBHelper.getInstance(this).addDummyContactIfNotExist();
        XMPPService.startService(MainActivity.this);

        initViews();
        setNavigation(savedInstanceState);

        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
        locManager = LocManager.getInstance(getApplicationContext());
        locManager.buildApiClient();

        {
            //TODO temporary snippet, will remove it.
            if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                ContactsHandler.getInstance().addCallToProcessPendingActions(this);
            } else {
                if (PermissionUtil.getInstance().requestPermission(this, new ArrayList<String>(Arrays.asList(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)))) {
                    ContactsHandler.getInstance().addCallToProcessPendingActions(this);
                }
            }
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences != null) {
            boolean sentToken = preferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
            if (!sentToken) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        backgroundDimmer = findViewById(R.id.background_dimmer);
        fabMenu.hideMenuButton(false);
        setFabMenuListeners(fabMenu);

        LayoutTransition lt = new LayoutTransition();
        lt.setStartDelay(LayoutTransition.APPEARING, 0);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.childFragmentContainer);
        frameLayout.setLayoutTransition(lt);
    }

    private void sendMixpaneldata() {
        try {
            JSONObject props = new JSONObject();
            props.put("User", TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_USER_JID) + "@mm.io");
            mixpanel.track("MainActivity - onCreate called", props);
        } catch (JSONException e) {
            Log.i("SPU", "Unable to add properties to JSONObject", e);
        }
        mixpanel.flush();
    }


    private void setFabMenuListeners(final FloatingActionMenu fabMenu) {

        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabMenu.isOpened()) {
                    //nothing
                } else {
                    backgroundDimmer.setVisibility(View.VISIBLE);
                }
                fabMenu.toggle(true);
            }
        });

        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    //do nothing
                } else {
                    backgroundDimmer.setVisibility(View.GONE);
                }
            }
        });

        FloatingActionButton peopleAroundMeFab = (FloatingActionButton) findViewById(R.id.people_around_me);
        peopleAroundMeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                   // Intent intent = new Intent(MainActivity.this, PeopleAroundMeMap.class);
                    Intent intent = new Intent(MainActivity.this, PeopleAroundActivity.class);
                    startActivity(intent);
                } else {
                    if (PermissionUtil.getInstance().requestPermission(MainActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)), getResources().getString(R.string.location_permission_message), Constants.REQUEST_CODE_LOCATION_PERMISSION)) {
                       // Intent intent = new Intent(MainActivity.this, PeopleAroundMeMap.class);
                        Intent intent = new Intent(MainActivity.this, PeopleAroundActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        FloatingActionButton createGroupFab = (FloatingActionButton) findViewById(R.id.create_group);
        createGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupDetailActivity.class);
                startActivity(intent);
            }
        });
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

        name.setText(contact.getName());

        viewMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.putExtra(Constants.IS_OWN_PROFILE, true);
                intent.putExtra("name", contact.getName());
                intent.putExtra("profilePicture", contact.image);
                intent.putExtra("status", contact.status);
                startActivity(intent);
                closeDrawer();
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
    protected void onStop() {
        super.onStop();
        if (fabMenu != null) {
            if (fabMenu.isOpened()) {
                fabMenu.close(false);
            }
        }
        if (shouldCloseDrawer) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locManager.connect();
    }

    private void initViews() {
        Toolbar toolbar = initToolBar();
        initiateDrawer();
        String titles[] = {getString(R.string.scores), getString(R.string.news), getString(R.string.messages)};
        int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapterInMainActivity(getSupportFragmentManager(), titles, numberOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ControlledSwipeViewPager) findViewById(com.sports.unity.R.id.pager);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(onPageChangeListener);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
        tabs.setCustomTabView(R.layout.sliding_tab_layout, R.id.title_text);
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
        View v = tabs.getTabStrip().getChildAt(2);
        unreadCount = (TextView) v.findViewById(R.id.unread_messages);

        //set news pager as default
        int tab_index = getIntent().getIntExtra("tab_index", 1);
        pager.setCurrentItem(tab_index);
        pager.setOffscreenPageLimit(2);
    }

    private void initiateDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                navigationFragment.updatePendingFriendRequestCount();

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

    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 2) {
                setUnreadCountToNull();
                messagesFragmentInFront = true;
                if (fabMenu != null) {
                    fabMenu.showMenuButton(true);
                }
            } else {
                messagesFragmentInFront = false;
                if (fabMenu != null) {
                    fabMenu.hideMenuButton(true);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setUnreadCountToNull() {
        if (unreadCount != null) {
            unreadCount.setVisibility(View.GONE);
        }
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

    public void updateUnreadMessages(int messagesCount) {
        int uCount = messagesCount;
        if (messagesFragmentInFront == false) {
            if (unreadCount != null) {
                if (uCount > 0) {
                    if (uCount > 99) {
                        unreadCount.setText(Html.fromHtml("99<sup>+</sup>"));
                    } else {
                        unreadCount.setText("" + uCount);
                    }
                    unreadCount.setVisibility(View.VISIBLE);
                } else {
                    unreadCount.setVisibility(View.GONE);
                }
            }
        } else {
            //do nothing
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shouldCloseDrawer = false;
        setNavigationProfile();
        updateLocation();
        if (messagesFragmentInFront) {
            fabMenu.showMenuButton(true);
        }
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
        shouldCloseDrawer = true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView != null && !searchView.isIconified()) {
            searchView.clearFocus();
            searchView.setIconified(true);
            //Intentionally written twice, please don't delete.
            searchView.setIconified(true);

        } else if (fabMenu != null && fabMenu.isOpened()) {
            fabMenu.toggle(true);
        } else {
            super.onBackPressed();
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
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fabMenu.showMenuButton(true);
        tabs.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);
        findViewById(R.id.seperator).setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_theme_blue));
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        initiateDrawer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        pager.setPagingEnabled(true);
    }


    public void enableSearch() {
        fabMenu.hideMenuButton(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toolbar.setNavigationIcon(R.drawable.ic_menu_back_blk);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tabs.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        findViewById(R.id.seperator).setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.textColorPrimary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        }
        pager.setPagingEnabled(false);
    }

    public void notifyToContactFragment() {
        if (this.contactSyncListener != null) {
            contactSyncListener.onSyncComplete();
        }
    }

    public interface PermissionResultHandler {
        public void onPermissionResult(int requestCode, int[] grantResults);
    }

    public void addContactSyncListener(ContactSyncListener listener) {
        this.contactSyncListener = listener;
    }

    public void removeContactSyncListener() {
        this.contactSyncListener = null;
    }

    public interface ContactSyncListener {
        public void onSyncComplete();
    }

    @Override
    public void onConnectionReplaced(Exception e) {
        super.onConnectionReplaced(e);
        if (!isConnectionReplaced) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showConnectionReplacedDialog();
                }
            });
        }
    }

    public void showConnectionReplacedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.connection_replaced_msg);
        builder.setPositiveButton(R.string.verify, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserUtil.setOtpSent(MainActivity.this, false);
                UserUtil.setUserRegistered(MainActivity.this, false);
                Intent intent = new Intent(MainActivity.this, EnterPhoneActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
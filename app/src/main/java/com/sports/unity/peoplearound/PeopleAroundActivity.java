package com.sports.unity.peoplearound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.SettingsActivity;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.messages.controller.model.User;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.FirebaseUtil;
import com.sports.unity.util.network.LocManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PeopleAroundActivity extends CustomAppCompatActivity implements PlaceSelectionListener {

    private static final String REQUEST_LISTENER_KEY = "nearby_key";
    private static final String REQUEST_TAG = "nearby_tag";
    public static final String BUNDLE_TAG = "bundle_tag";

    public static final String FRIENDS_KEY = "friends";
    public static final String SPU_KEY = "spu";
    public static final String SIMILAR_USERS_KEY = "similar";

    public static final int fetchDataCode = 001;

    private int defaultRadius = 40000;
    private int radius = 1000;
    private int stepRange = 25;

    private TextView titleAddress;

    String[] titles = {"FRIENDS", "SU USERS", "SIMILAR USERS"};

    private SlidingTabLayout tabs;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private Location mLastKnownLocation;
    private TinyDB tinyDB;
    private ViewPager viewPager;

    private static HashMap<String, DataNotifier> listenersMap = new HashMap<>();

    ArrayList<User> friends = new ArrayList<>();
    ArrayList<User> sportsUnityUsers = new ArrayList<>();
    ArrayList<User> similarUsers = new ArrayList<>();

    private ScoresContentHandler.ContentListener contentListener = new ScoresContentHandler.ContentListener() {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if (responseCode == 200) {
                handleData(content, responseCode);
            } else {
                listenersMap.get(FRIENDS_KEY).newData(null, responseCode);
                listenersMap.get(SPU_KEY).newData(null, responseCode);
                listenersMap.get(SIMILAR_USERS_KEY).newData(null, responseCode);
            }
        }
    };

    private void logScreensToFireBase(String eventName) {
        //FIREBASE INTEGRATION
        {
            FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(PeopleAroundActivity.this);
            Bundle bundle = new Bundle();
            FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName);
        }
    }

    private void handleData(String content, int responseCode) {
        friends.clear();
        sportsUnityUsers.clear();
        similarUsers.clear();

        try {
            JSONObject data = new JSONObject(content);

            JSONArray usersArray = data.getJSONArray("users");
            if (usersArray.length() > 0) {
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = (JSONObject) usersArray.get(i);
                    if (user.getString("friendship_status").equals("friends")) {
                        friends.add(new User(user.getString("name"), user.getString("username"), (int) Math.round(user.getDouble("distance")), user.getString("last_seen"), user.getBoolean("is_available")));
                    } else {
                        boolean success = false;
                        JSONArray interests = user.getJSONArray("interests");

                        ArrayList<FavouriteItem> selfInterests = FavouriteItemWrapper.getInstance(this).getFavList();

                        if (selfInterests.size() > 0) {

                            if (interests != null && interests.length() > 0) {
                                for (int j = 0; j < interests.length(); j++) {
                                    String st = interests.getString(j);
                                    for (FavouriteItem item : selfInterests) {
                                        if (item.getName().equals(st)) {
                                            similarUsers.add(new User(user.getString("name"), user.getString("username"), (int) Math.round(user.getDouble("distance")), user.getString("last_seen"), user.getBoolean("is_available")));
                                            success = true;
                                            break;
                                        }
                                    }
                                    if (success) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            //do nothing
                        }

                        if (success) {
                            //do nothing
                        } else {
                            sportsUnityUsers.add(new User(user.getString("name"), user.getString("username"), (int) Math.round(user.getDouble("distance")), user.getString("last_seen"), user.getBoolean("is_available")));
                        }
                    }
                }
            } else {
                //TODO
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(friends);
        Collections.sort(similarUsers);
        Collections.sort(sportsUnityUsers);

        listenersMap.get(FRIENDS_KEY).newData(friends, responseCode);
        listenersMap.get(SPU_KEY).newData(sportsUnityUsers, responseCode);
        listenersMap.get(SIMILAR_USERS_KEY).newData(similarUsers, responseCode);

        Log.i("listeners", String.valueOf(friends.size()) + " --> " + String.valueOf(sportsUnityUsers.size()) + " --> " + String.valueOf(similarUsers.size()));
    }


    public static void addListener(DataNotifier dataNotifier, String string) {
        listenersMap.put(string, dataNotifier);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);
        if (UserUtil.isShowMyLocation()) {
            checkIfGPSEnabled();
        } else {
            promptDialogToEnableLocationSettings();
        }

    }

    private void promptDialogToEnableLocationSettings() {
        AlertDialog.Builder build = new AlertDialog.Builder(PeopleAroundActivity.this);
        build.setTitle("People Around Me");
        build.setMessage("Turn on location settings for other fans to find you");
        build.setPositiveButton("ENABLE LOCATION", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(Constants.ENABLE_LOCATION, Constants.CHECK_LOCATION);
                startActivity(intent);
            }

        });
        build.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                PeopleAroundActivity.this.finish();
            }

        });

        AlertDialog dialog = build.create();
        dialog.setCancelable(false);
        dialog.show();
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.app_theme_blue));
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.app_theme_blue));
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScoresContentHandler.getInstance().removeResponseListener(REQUEST_LISTENER_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_around);

        radius = defaultRadius;
        CommonUtil.sendAnalyticsData(getApplication(), "PeopleAroundMeScreen");
        tinyDB = TinyDB.getInstance(getApplicationContext());
        initToolbar();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PeopleAroundMeViewPagerAdapter(getSupportFragmentManager(), titles, titles.length));

        tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
        tabs.setCustomTabView(R.layout.sliding_tab_layout, R.id.title_text);
        tabs.setDistributeEvenly(true);


        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorPrimary);
            }
        });

        tabs.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        initLocationButton();
        InitSeekbar();

        int position = getIntent().getIntExtra("tabPosition", 1);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        logScreensToFireBase(FirebaseUtil.Event.PAM_FRIENDS_TAB);
                        break;
                    case 1:
                        logScreensToFireBase(FirebaseUtil.Event.PAM_SU_TAB);
                        break;
                    case 2:
                        logScreensToFireBase(FirebaseUtil.Event.PAM_SIMILAR_TAB);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initLocationButton() {
        final FloatingActionButton myLocationButton = (FloatingActionButton) findViewById(R.id.update_location);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    private void updateStreetAddressOnToolbar(final Location mLastKnownLocation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocManager.getInstance(getApplicationContext()).saveLocation(mLastKnownLocation);
                titleAddress.post(new Runnable() {
                    @Override
                    public void run() {
                        titleAddress.setText(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_LOCATION));
                    }
                });
            }
        }).start();
    }

    private void getPeopleAroundMe(double latitude, double longitude) {
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_USERNAME, tinyDB.getString(TinyDB.KEY_USER_JID));
        parameters.put(ScoresContentHandler.PARAM_PASSWORD, tinyDB.getString(TinyDB.KEY_PASSWORD));
        parameters.put(ScoresContentHandler.PARAM_LATITUDE, String.valueOf(latitude));
        parameters.put(ScoresContentHandler.PARAM_LONGITUDE, String.valueOf(longitude));
        parameters.put(ScoresContentHandler.PARAM_RADIUS, String.valueOf(radius));
        parameters.put(Constants.REQUEST_PARAMETER_KEY_APK_VERSION, CommonUtil.getBuildConfig());
        parameters.put(Constants.REQUEST_PARAMETER_KEY_UDID, CommonUtil.getDeviceId(this));
        ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_NEAR_BY_USERS, parameters, REQUEST_LISTENER_KEY, REQUEST_TAG);
        if (listenersMap.size() > 0) {
            listenersMap.get(FRIENDS_KEY).newData(friends, fetchDataCode);
            listenersMap.get(SPU_KEY).newData(sportsUnityUsers, fetchDataCode);
            listenersMap.get(SIMILAR_USERS_KEY).newData(similarUsers, fetchDataCode);
        }
    }

    public boolean checkIfGPSEnabled() {
        boolean succcess = false;
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            showDialogToPromptEnableGps();
        } else {
            updateUsers();
            succcess = true;
        }
        return succcess;
    }

    private void updateUsers() {
        if (mLastKnownLocation == null) {
            getCurrentLocation();
        } else {
            getPeopleAroundMe(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        }
    }

    private void getCurrentLocation() {
        Location location = LocManager.getInstance(getApplicationContext()).getLocation(PeopleAroundActivity.this);
        if (location != null) {
            mLastKnownLocation = location;
            updateStreetAddressOnToolbar(mLastKnownLocation);
            getPeopleAroundMe(location.getLatitude(), location.getLongitude());
            LocManager.getInstance(getApplicationContext()).sendLatituteAndLongitude(mLastKnownLocation, true);
        } else {
            Toast.makeText(getApplicationContext(), "could not get location", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogToPromptEnableGps() {
        AlertDialog.Builder build = new AlertDialog.Builder(PeopleAroundActivity.this);
        build.setTitle("People Around Me");
        build.setMessage("Turn on location services for other users to see you");
        build.setPositiveButton("ENABLE GPS", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }

        });
        build.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                PeopleAroundActivity.this.finish();
            }

        });

        AlertDialog dialog = build.create();
        dialog.setCancelable(false);
        dialog.show();
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.app_theme_blue));
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.app_theme_blue));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_map);
        titleAddress = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleAddress.setSelected(true);

        ImageView closeButton = (ImageView) toolbar.findViewById(R.id.close_icon);
        ImageView refreshUsersButton = (ImageView) toolbar.findViewById(R.id.refresh);
        ImageView privacySettingsButton = (ImageView) toolbar.findViewById(R.id.privacy_icon);

        closeButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        refreshUsersButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        privacySettingsButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));

        titleAddress.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        titleAddress.setText(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_LOCATION));

        closeButton.setOnClickListener(onClickListener);
        refreshUsersButton.setOnClickListener(onClickListener);
        privacySettingsButton.setOnClickListener(onClickListener);
        titleAddress.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.close_icon) {
                onBackPressed();
            } else if (v.getId() == R.id.refresh) {
                updateUsers();
            } else if (v.getId() == R.id.privacy_icon) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(Constants.ENABLE_LOCATION, Constants.CHECK_LOCATION);
                startActivity(intent);
            } else if (v.getId() == R.id.toolbar_title) {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                        .build();
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(typeFilter)
                                    .build(PeopleAroundActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("status", status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void InitSeekbar() {
        TextView distanceText = (TextView) findViewById(R.id.distance_text);
        distanceText.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
        SeekBar seekDistance = (SeekBar) findViewById(R.id.distance_seekbar);
        seekDistance.setMax(100);
        seekDistance.setProgress(100);
        seekDistance.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_40));
        seekDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                logScreensToFireBase(FirebaseUtil.Event.PAM_SLIDER);
                int progress = seekBar.getProgress();
                if (mLastKnownLocation == null) {
                    seekBar.setProgress(0);
                    getCurrentLocation();
                } else {
                    if (progress >= 0 && progress <= stepRange / 2) {
                        seekBar.setProgress(0);
                        seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_01));
                        radius = 1000;
                        getPeopleAroundMe(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                    } else if (progress >= 0 * stepRange + stepRange / 2 && progress <= 0 * stepRange + stepRange / 2 + stepRange) {
                        seekBar.setProgress(1 * stepRange);
                        seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_05));
                        radius = 5000;
                        getPeopleAroundMe(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                    } else if (progress >= 1 * stepRange + stepRange / 2 && progress <= 1 * stepRange + stepRange / 2 + stepRange) {
                        seekBar.setProgress(2 * stepRange);
                        seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_20));
                        radius = 20000;
                        getPeopleAroundMe(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                    } else if (progress >= 2 * stepRange + stepRange / 2 && progress <= 2 * stepRange + stepRange / 2 + stepRange) {
                        seekBar.setProgress(3 * stepRange);
                        seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_30));
                        radius = 30000;
                        getPeopleAroundMe(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                    } else {
                        seekBar.setProgress(4 * stepRange);
                        seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_40));
                        radius = 40000;
                        getPeopleAroundMe(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    }
                }
            }

        });
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i("address", String.valueOf(place.getAddress()));
        titleAddress.setText(place.getAddress());
        LatLng selectedPlaceLatlng = place.getLatLng();
        mLastKnownLocation = null;
        mLastKnownLocation = new Location("");
        mLastKnownLocation.setLatitude(selectedPlaceLatlng.latitude);
        mLastKnownLocation.setLongitude(selectedPlaceLatlng.longitude);
        updateStreetAddressOnToolbar(mLastKnownLocation);
        updateUsers();
    }

    @Override
    public void onError(Status status) {
        //TODO
    }
}

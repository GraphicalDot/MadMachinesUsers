package com.sports.unity.peoplearound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.sports.unity.R;
import com.sports.unity.common.controller.SettingsActivity;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.messages.controller.model.User;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.LocManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PeopleAroundActivity extends AppCompatActivity {

    private static final String REQUEST_LISTENER_KEY = "nearby_key";
    private static final String REQUEST_TAG = "nearby_tag";
    public static final String BUNDLE_TAG = "bundle_tag";

    public static final String FRIENDS_KEY = "friends";
    public static final String SPU_KEY = "spu";
    public static final String SIMILAR_USERS_KEY = "similar";


    private int radius = 1000;
    private int stepRange = 25;

    private TextView titleAddress;
    private TextView titleCity;

    String[] titles = {"FRIENDS", "SU USERS", "SIMILAR USERS"};

    private SlidingTabLayout tabs;

    private Location mLastKnownLocation;
    private TinyDB tinyDB;
    private ViewPager viewPager;
    private ProgressBar progressBar;

    private static HashMap<String, DataNotifier> listenersMap = new HashMap<>();

    private ScoresContentHandler.ContentListener contentListener = new ScoresContentHandler.ContentListener() {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            if (responseCode == 200) {
                handleData(content);
            } else {
                listenersMap.get(FRIENDS_KEY).newData(null);
                listenersMap.get(SPU_KEY).newData(null);
                listenersMap.get(SIMILAR_USERS_KEY).newData(null);
            }
        }
    };

    private void handleData(String content) {
        ArrayList<User> friends = new ArrayList<>();
        ArrayList<User> sportsUnityUsers = new ArrayList<>();
        ArrayList<User> similarUsers = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(content);

            JSONArray usersArray = data.getJSONArray("users");
            if (usersArray.length() > 0) {
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = (JSONObject) usersArray.get(i);
                    if (user.getString("friendship_status").equals("friends")) {
                        friends.add(new User(user.getString("name"), user.getString("username"), (int) Math.round(user.getDouble("distance"))));
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
                                            similarUsers.add(new User(user.getString("name"), user.getString("username"), (int) Math.round(user.getDouble("distance"))));
                                            success = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            //do nothing
                        }

                        if (success) {
                            //do nothing
                        } else {
                            sportsUnityUsers.add(new User(user.getString("name"), user.getString("username"), (int) Math.round(user.getDouble("distance"))));
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

        listenersMap.get(FRIENDS_KEY).newData(friends);
        listenersMap.get(SPU_KEY).newData(sportsUnityUsers);
        listenersMap.get(SIMILAR_USERS_KEY).newData(similarUsers);

        Log.i("listeners", String.valueOf(friends.size()) + " --> " + String.valueOf(sportsUnityUsers.size()) + " --> " + String.valueOf(similarUsers.size()));
    }


    public static void addListener(DataNotifier dataNotifier, String string) {
        listenersMap.put(string, dataNotifier);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);
        checkIfGPSEnabled();
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

        tinyDB = TinyDB.getInstance(getApplicationContext());
        initToolbar();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
                        titleCity.setText(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_STATE));
                    }
                });
            }
        }).start();
    }

    private void getPeopleAroundMe(double latitude, double longitude) {
        viewPager.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
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
            getCurrentLocation();
            succcess = true;
        }
        return succcess;
    }

    private void getCurrentLocation() {
        Location location = LocManager.getInstance(getApplicationContext()).getLocation(PeopleAroundActivity.this);
        if (location != null) {
            mLastKnownLocation = location;
            updateStreetAddressOnToolbar(mLastKnownLocation);
            getPeopleAroundMe(location.getLatitude(), location.getLongitude());
            LocManager.getInstance(getApplicationContext()).sendLatituteAndLongitude(mLastKnownLocation, true);
        } else {
            Toast.makeText(getApplicationContext(), "location is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogToPromptEnableGps() {
        AlertDialog.Builder build = new AlertDialog.Builder(PeopleAroundActivity.this);
        build.setTitle("People Around Me");
        build.setMessage("Turn on location settings for other fans to see you");
        build.setPositiveButton("ENABLE LOCATION", new DialogInterface.OnClickListener() {

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
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_map);
        titleAddress = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleCity = (TextView) toolbar.findViewById(R.id.secondary_title);

        ImageView closeButton = (ImageView) toolbar.findViewById(R.id.close_icon);
        ImageView refreshUsersButton = (ImageView) toolbar.findViewById(R.id.refresh);
        ImageView privacySettingsButton = (ImageView) toolbar.findViewById(R.id.privacy_icon);

        closeButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        refreshUsersButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        privacySettingsButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));

        titleAddress.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        titleCity.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        titleAddress.setText(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_LOCATION));
        titleCity.setText(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_STATE));

        closeButton.setOnClickListener(onClickListener);
        refreshUsersButton.setOnClickListener(onClickListener);
        privacySettingsButton.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.close_icon) {
                onBackPressed();
            } else if (v.getId() == R.id.refresh) {
                getPeopleAroundMe(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            } else if (v.getId() == R.id.privacy_icon) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(Constants.ENABLE_LOCATION, Constants.CHECK_LOCATION);
                startActivity(intent);
            }
        }
    };

    private void InitSeekbar() {
        TextView distanceText = (TextView) findViewById(R.id.distance_text);
        distanceText.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
        SeekBar seekDistance = (SeekBar) findViewById(R.id.distance_seekbar);
        seekDistance.setMax(100);
        seekDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
}

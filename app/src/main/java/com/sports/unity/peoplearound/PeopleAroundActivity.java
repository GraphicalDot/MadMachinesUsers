package com.sports.unity.peoplearound;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.SettingsActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.gcm.TokenRegistrationHandler;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.activity.PeopleService;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.NearByUserJsonCaller;
import com.sports.unity.messages.controller.model.PeoplesNearMe;
import com.sports.unity.messages.controller.model.Person;
import com.sports.unity.scores.DataServiceContract;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.LocManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.sports.unity.common.model.TinyDB.KEY_CURRENT_LATITUDE;
import static com.sports.unity.common.model.TinyDB.KEY_CURRENT_LONGITUDE;
import static com.sports.unity.common.model.TinyDB.KEY_PASSWORD;
import static com.sports.unity.common.model.TinyDB.KEY_USER_JID;
import static com.sports.unity.common.model.TinyDB.getInstance;
import static com.sports.unity.scores.model.ScoresContentHandler.CALL_NAME_NEAR_BY_USERS;
import static com.sports.unity.scores.model.ScoresContentHandler.PARAM_LATITUDE;
import static com.sports.unity.scores.model.ScoresContentHandler.PARAM_LONGITUDE;
import static com.sports.unity.scores.model.ScoresContentHandler.PARAM_PASSWORD;
import static com.sports.unity.scores.model.ScoresContentHandler.PARAM_RADIUS;
import static com.sports.unity.scores.model.ScoresContentHandler.PARAM_USERNAME;
import static com.sports.unity.util.CommonUtil.getBuildConfig;
import static com.sports.unity.util.CommonUtil.getDeviceId;
import static com.sports.unity.util.Constants.REQUEST_PARAMETER_KEY_APK_VERSION;
import static com.sports.unity.util.Constants.REQUEST_PARAMETER_KEY_UDID;

public class PeopleAroundActivity extends AppCompatActivity implements PeopleService,TokenRegistrationHandler.TokenRegistrationContentListener {

    private static final String REQUEST_LISTENER_KEY = "nearby_key";
    private static final String REQUEST_TAG = "nearby_tag";
    private static final String SPORT_SELECTION_FOOTBALL = "football";
    private static final String SPORT_SELECTION_CRICKET = "cricket";


    private Dialog aDialog = null;

    private NearByUserJsonCaller nearByUserJsonCaller = new NearByUserJsonCaller();

    //private GoogleMap map;
    private LatLng latLong;
    private String sportSelection = SPORT_SELECTION_FOOTBALL;

    private Toolbar toolbar;
    private TextView titleAddress;
    private TextView titleCity;
    private boolean customLocation;
    private LocManager locManager;
    private int radius = 1000;

    private int stepRange = 25;

    private boolean profilefetching = false;
    private ProgressDialog dialog = null;

    private PeoplesNearMe peoplesNearMe;
    private boolean userLocation;
    private TokenRegistrationHandler tokenRegistrationHandler;
    private ViewPager mViewPager;
    private PeopleAroundMeViewPagerAdapter peopleAroundMeViewPagerAdapter;

    private ArrayList<Person> peopleFriends = new ArrayList<>();
    private ArrayList<Person> peopleSU = new ArrayList<>();
    private ArrayList<Person> peopleNeedHeading = new ArrayList<>();
    private  ImageView refreshButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_around);
        this.customLocation = false;
        aDialog = new Dialog(PeopleAroundActivity.this);
        aDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        locManager = LocManager.getInstance(getApplicationContext());
        locManager.buildApiClient();
       // setCustomButtonsForNavigationAndUsers();
        hideSoftKeyboard();
        initToolbar();
        InitSeekbar();
        bindAutoComplete();
        userPrivacyUpdate();
        getLocation();
       // getPeopleAroundMe(latLong.latitude, latLong.longitude);
        int tab_index = 0;
        mViewPager = (ViewPager) findViewById(R.id.pager);
        String peopleAroundMeTitles[] = {getString(R.string.friends_tab), getString(R.string.su_users_tab), getString(R.string.same_interest)};
        int peopleAroundMeTabs = peopleAroundMeTitles.length;

        peopleAroundMeViewPagerAdapter = new PeopleAroundMeViewPagerAdapter(getSupportFragmentManager(), peopleAroundMeTitles, peopleAroundMeTabs,peopleFriends,peopleSU,peopleNeedHeading);
        mViewPager.setAdapter(peopleAroundMeViewPagerAdapter);
        tab_index = getIntent().getIntExtra("tab_index", 1);
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        //tabs.setTabTextColor(R.color.filter_tab_selector);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorPrimary);
            }
        });
        tabs.setViewPager(mViewPager);
        mViewPager.setCurrentItem(tab_index);

    }



    private ScoresContentHandler.ContentListener contentListener = new ScoresContentHandler.ContentListener() {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLong);

            if (tag.equals(REQUEST_TAG)) {
                if (responseCode == 200) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    peoplesNearMe = ScoresJsonParser.parseListOfNearByUsers(content);
                      ArrayList<Person> people = peoplesNearMe.getPersons();
                    peopleFriends.clear();
                    peopleSU.clear();
                    peopleNeedHeading.clear();
                    Collections.sort(people);
                    for(Person person : people){
                     if(person.isFriend()){
                         peopleFriends.add(person);

                     }else if(person.isCommonInterest()){
                         peopleNeedHeading.add(person);
                     }else{
                         peopleSU.add(person);
                     }




                    }
                  //  int count = peopleAroundMeViewPagerAdapter.getCount();
                    //getSupportFragmentManager().getFragments();
                    for(Fragment fragment: getSupportFragmentManager().getFragments()){
//                        Fragment
//                                fragment= peopleAroundMeViewPagerAdapter.getItem(i);

                        if(fragment instanceof DataNotifier) {
                            DataNotifier listner = (DataNotifier)fragment;
                            listner.notifyPeoples();
                        }
                    }

                    if (dialog != null) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                    // map.moveCamera(CameraUpdateFactory.zoomTo(calculateZoomLevel(radius)));
                      /*  map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, getcurrentZoom()));*/
                    LayoutInflater inflater = PeopleAroundActivity.this.getLayoutInflater();
                    View view = inflater.inflate(R.layout.chat_other_profile_layout, null);
//                        AlertDialog.Builder otherProfileBuilder = new AlertDialog.Builder(PeopleAroundMeMap.this);
//                        otherProfileBuilder.setView(view);
//                        aDialog = otherProfileBuilder.create();
//                        aDialog.getWindow().setBackgroundDra'getDrawable(int)' is deprecated more... (Ctrl+wable(new ColorDrawable(Color.TRANSPARENT));
//                        aDialog.show();
                    aDialog.setContentView(R.layout.chat_other_profile_layout);
                    aDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //aDialog.show();
                    populateProfilePopup(null, view, null, 0, null, null);

                } else {
                    if (dialog != null) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                    makeText(getApplicationContext(), "Something went wrong please try again", LENGTH_LONG).show();
                }
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        }

    };


    private void userPrivacyUpdate() {
        userLocation = UserUtil.isShowToAllLocation();
        tokenRegistrationHandler = TokenRegistrationHandler.getInstance(getApplicationContext());
        tokenRegistrationHandler.addListener(this);
        tokenRegistrationHandler.setUserPrivacyPolicy(userLocation);
    }

    private void bindAutoComplete() {
        PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.custom_location);
        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                customLocation = true;
                findViewById(R.id.fl_custom_location).setVisibility(GONE);
                getInstance(getApplicationContext()).putDouble(KEY_CURRENT_LATITUDE, place.getLatLng().latitude);
                getInstance(getApplicationContext()).putDouble(KEY_CURRENT_LONGITUDE, place.getLatLng().longitude);
                titleAddress.setText(place.getAddress());
                titleCity.setText(place.getName());
                latLong = place.getLatLng();
                getPeopleAroundMe(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                customLocation = false;
                findViewById(R.id.fl_custom_location).setVisibility(GONE);
                Toast.makeText(getApplicationContext(), getText(R.string.oops_try_again), LENGTH_LONG).show();
            }
        });
    }
    public void setCustomButtonsForNavigationAndUsers() {
       /*FloatingActionButton myLocation = (FloatingActionButton)findViewById(R.id.myLocation);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customLocation = false;
                boolean success = checkIfGPSEnabled();
                if (success) {
                    getLocation();
                }
            }
        });*/
        /*ImageView refreshUsers = (ImageView) findViewById(R.id.refreshUsers);
        refreshUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latLong != null) {
                    getPeopleAroundMe(latLong.latitude, latLong.longitude);
                }

            }
        });*/
    }



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
                if (progress >= 0 && progress <= stepRange / 2) {
                    seekBar.setProgress(0);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_01));
                    radius = 1000;

                    //map.animateCamera(CameraUpdateFactory.zoomTo(calculateZoomLevel(radius)));
                    fetchUsersNearByWithNewRadius();

                } else if (progress >= 0 * stepRange + stepRange / 2 && progress <= 0 * stepRange + stepRange / 2 + stepRange) {
                    seekBar.setProgress(1 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_05));
                    radius = 5000;
                    //map.animateCamera(CameraUpdateFactory.zoomTo(calculateZoomLevel(radius)));
                    fetchUsersNearByWithNewRadius();
                } else if (progress >= 1 * stepRange + stepRange / 2 && progress <= 1 * stepRange + stepRange / 2 + stepRange) {
                    seekBar.setProgress(2 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_20));
                    radius = 20000;
                    //map.animateCamera(CameraUpdateFactory.zoomTo(calculateZoomLevel(radius)));
                    fetchUsersNearByWithNewRadius();
                } else if (progress >= 2 * stepRange + stepRange / 2 && progress <= 2 * stepRange + stepRange / 2 + stepRange) {
                    seekBar.setProgress(3 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_30));
                    radius = 30000;
                    // map.animateCamera(CameraUpdateFactory.zoomTo(calculateZoomLevel(radius)));
                    fetchUsersNearByWithNewRadius();
                } else {
                    seekBar.setProgress(4 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_40));
                    radius = 40000;

                    //map.animateCamera(CameraUpdateFactory.zoomTo(calculateZoomLevel(radius)));
                    fetchUsersNearByWithNewRadius();
                }

            }

        });
    }

    private void fetchUsersNearByWithNewRadius() {
        getPeopleAroundMe(latLong.latitude, latLong.longitude);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar_map);
        titleAddress = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleAddress.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        titleCity = (TextView) toolbar.findViewById(R.id.secondary_title);
        titleCity.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        setCurrentAddressOnToolbar(titleAddress, titleCity);
        //toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.findViewById(R.id.close_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.findViewById(R.id.privacy_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();

                checkAndEnableLocation();

            }
        });
        toolbar.findViewById(R.id.myaddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.fl_custom_location).setVisibility(VISIBLE);
            }
        });
        toolbar.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPeopleAroundMe(latLong.latitude, latLong.longitude);
            }
        });
    }

    private void checkAndEnableLocation() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        intent.putExtra(Constants.ENABLE_LOCATION, Constants.CHECK_LOCATION);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("After Permission" + intent, "onActivityResult: " + requestCode);
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSION) {
            if (resultCode == RESULT_OK) {
                Log.d("After Permission", "onActivityResult: ");
            }
        }
    }
    private void setCurrentAddressOnToolbar(TextView titleAddress, TextView titleCity) {
        titleAddress.setText(getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_LOCATION));
        titleCity.setText(getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_STATE));
    }


    private void getPeopleAroundMe(double latitude, double longitude) {

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

        dialog = ProgressDialog.show(PeopleAroundActivity.this, "",
                "fetching...", true);
        dialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(PARAM_USERNAME, getInstance(getApplicationContext()).getString(KEY_USER_JID));
        parameters.put(PARAM_PASSWORD, getInstance(getApplicationContext()).getString(KEY_PASSWORD));
        parameters.put(PARAM_LATITUDE, String.valueOf(latitude));
        parameters.put(PARAM_LONGITUDE, String.valueOf(longitude));
        parameters.put(PARAM_RADIUS, String.valueOf(radius));
        parameters.put(REQUEST_PARAMETER_KEY_APK_VERSION, getBuildConfig());
        parameters.put(REQUEST_PARAMETER_KEY_UDID, getDeviceId(this));
        ScoresContentHandler.getInstance().requestCall(CALL_NAME_NEAR_BY_USERS, parameters, REQUEST_LISTENER_KEY, REQUEST_TAG);
    }

    private boolean createContact(String jid, Context context, VCard vCard) {
        boolean success = false;
        SportsUnityDBHelper.getInstance(context).addToContacts(vCard.getNickName(), null, jid, ContactsHandler.getInstance().defaultStatus, null, Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME);
        SportsUnityDBHelper.getInstance(context).updateContacts(jid, vCard.getAvatar(), vCard.getMiddleName());
        return success;
    }

    private void renderProfile(final Person person) {
        LayoutInflater inflater = PeopleAroundActivity.this.getLayoutInflater();
        View popupProfile = inflater.inflate(R.layout.chat_other_profile_layout, null);

//        AlertDialog.Builder otherProfileBuilder = new AlertDialog.Builder(PeopleAroundMeMap.this);
//        otherProfileBuilder.setView(popupProfile);
        aDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                profilefetching = false;
            }

        });
//        otherProfileBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                profilefetching = false;
//            }
//
//        });

        aDialog.setContentView(R.layout.chat_other_profile_layout);
        aDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aDialog.show();

        aDialog.findViewById(R.id.progressBarProfile).setVisibility(VISIBLE);

//        int distance = (int) Math.round(Double.parseDouble(marker.getSnippet().substring(marker.getSnippet().indexOf(",") + 1, marker.getSnippet().length())));
        int distance = person.getDistance();
        new GetVcardForUser(popupProfile, distance, person).execute(person.getUsername());

    }

    @Override
    public boolean showProfile(Person person) {
        if (profilefetching == true) {
            //nothing
        } else {
            profilefetching = true;
            renderProfile(person);
        }
        return false;
    }

    @Override
    public void showCluster(Cluster<Person> cluster) {

    }


    private void populateProfilePopup(final VCard vCard, View popupProfile, final String jid, int distance, String info, Person person) {

        CircleImageView imageview = (CircleImageView) aDialog.findViewById(R.id.user_pic);

        TextView distancefromUser = (TextView) aDialog.findViewById(R.id.distanceFromMe);
        distancefromUser.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoMedium());

        TextView sport = (TextView) aDialog.findViewById(R.id.sport);
        sport.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        TextView name = (TextView) aDialog.findViewById(R.id.username);
        name.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());

        Button sayHello = (Button) aDialog.findViewById(R.id.start_chat);
        sayHello.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        ImageView dismissDialog = (ImageView) aDialog.findViewById(R.id.close_icon);
        dismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aDialog.cancel();
            }
        });
        aDialog.findViewById(R.id.progressBarProfile).setVisibility(GONE);

        if (vCard == null) {
            if (info == null) {
                sayHello.setVisibility(GONE);
                aDialog.findViewById(R.id.dot).setVisibility(GONE);
                sport.setVisibility(GONE);

                name.setText(R.string.no_users_nearby);
                distancefromUser.setText(R.string.try_broadening_search_increasing_radius);
                imageview.setImageResource(R.drawable.img_no_frnd_found);
            } else {
                sayHello.setVisibility(GONE);
                aDialog.findViewById(R.id.dot).setVisibility(GONE);
                sport.setVisibility(GONE);

                name.setText("Whoops!!");
                distancefromUser.setText(info);
                imageview.setImageResource(R.drawable.img_no_frnd_found);
            }

        } else {
            name.setText(vCard.getNickName());
            byte[] image = vCard.getAvatar();
            if (image != null) {
                imageview.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            if (distance > 1000) {
                float dist = distance /= 1000;
                distancefromUser.setText(String.valueOf(dist) + " kms ");
            } else {
                distancefromUser.setText(String.valueOf(distance) + " mts ");
            }


            sport.setText(" " + sportSelection);

            sayHello.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contacts contact = SportsUnityDBHelper.getInstance(getApplicationContext()).getContactByJid(jid);
                    if (contact == null) {
                        createContact(jid, getApplicationContext(), vCard);
                        contact = SportsUnityDBHelper.getInstance(getApplicationContext()).getContactByJid(jid);
                        moveToChatActivity(contact, false);
                    } else {
                        moveToChatActivity(contact, true);
                    }
                }
            });
        }
    }

    private void moveToChatActivity(Contacts contact, boolean contactAvailable) {
        String name = contact.getName();
        int contactId = contact.id;
        byte[] userPicture = contact.image;
        boolean nearbyChat = false;
        boolean blockStatus = SportsUnityDBHelper.getInstance(getApplicationContext()).isChatBlocked(contactId);
        boolean othersChat = contact.isOthers();

        Intent intent = ChatScreenActivity.createChatScreenIntent(this, false, contact.jid, name, contact.id, userPicture, blockStatus, othersChat, contact.availableStatus, contact.status);
        startActivity(intent);
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
            succcess = true;
        }
        return succcess;
    }

    private void showDialogToPromptEnableGps() {
        LayoutInflater inflater = PeopleAroundActivity.this.getLayoutInflater();
        View enableGps = inflater.inflate(R.layout.dialog_enable_gps, null);
        setCustomFont(enableGps);

        aDialog.setContentView(R.layout.dialog_enable_gps);
//        aDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aDialog.show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(PeopleAroundMeMap.this);
//        builder.setView(enableGps);
//
//        aDialog = builder.create();
//        aDialog.show();

        aDialog.findViewById(R.id.enable_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aDialog.cancel();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
    }

    private void setCustomFont(View enableGps) {
        TextView titleGps = (TextView) enableGps.findViewById(R.id.title_gps);
        titleGps.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());

        TextView gpsStatus = (TextView) enableGps.findViewById(R.id.gps_status);
        gpsStatus.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        TextView turnOnGps = (TextView) enableGps.findViewById(R.id.turn_on_gps);
        turnOnGps.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        Button goToLocationSettings = (Button) enableGps.findViewById(R.id.enable_gps);
        goToLocationSettings.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

    }

    public void getLocation() {
        Log.i("gettingLocation", "true");
        Location location = locManager.getLocation();
        if (location != null) {
            LocManager.getInstance(getApplicationContext()).sendLatituteAndLongitude(location, true);
            getInstance(getApplicationContext()).putDouble(KEY_CURRENT_LATITUDE, location.getLatitude());
            getInstance(getApplicationContext()).putDouble(KEY_CURRENT_LONGITUDE, location.getLongitude());
            latLong = new LatLng(location.getLatitude(), location.getLongitude());
            // map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, getcurrentZoom()));
            new FetchAndDisplayCurrentAddress(location).execute();
            getPeopleAroundMe(latLong.latitude, latLong.longitude);
        }

    }

    /*private float getcurrentZoom() {
        return map.getCameraPosition().zoom;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "called");
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);

        if (checkIfGPSEnabled()) {

            Location location = LocManager.getInstance(getApplicationContext()).getLocation();
            if (location != null) {
                LocManager.getInstance(getApplicationContext()).sendLatituteAndLongitude(location, true);
            }
        } else {
            //nothing
        }
        userPrivacyUpdate();
        // loadMap();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void dismissDialog() {
        if (aDialog != null && aDialog.isShowing()) {
            aDialog.cancel();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPaused", "called");
        dismissDialog();
        ScoresContentHandler.getInstance().removeResponseListener(REQUEST_LISTENER_KEY);
    }

    private void onUnSuccessfulVcardRetrieval(View view, Person person) {
        String info = "Something went wrong";
        populateProfilePopup(null, view, null, 0, info, person);
        //TODO
    }

    private void onSuccessfulVcardRetrieval(View view, VCard vCard, String jid, int distance, Person person) {
        populateProfilePopup(vCard, view, jid, distance, null, person);
    }


    @Override
    protected void onStart() {
        super.onStart();
        locManager.connect();
    }

    @Override
    public void handleContent(String content) {
        try {
            JSONObject object = new JSONObject(content);
            if(object!=null && !object.isNull("status") ){

                if(200 == object.getInt("status") && !object.isNull("info") && object.getString("info").equalsIgnoreCase("Success")){
                    Toast.makeText(this,R.string.privacy_policy_status_success,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,R.string.privacy_policy_status,Toast.LENGTH_SHORT).show();

                }

            }
        }catch (Exception e){

        }

    }

    class FetchAndDisplayCurrentAddress extends AsyncTask<Void, Void, Void> {

        Location location = null;

        public FetchAndDisplayCurrentAddress(Location location) {
            this.location = location;
        }

        @Override
        protected Void doInBackground(Void... params) {
            LocManager.getInstance(getApplicationContext()).saveLocation(location);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            titleAddress.setText(getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_LOCATION));
            titleCity.setText(getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_STATE));
            super.onPostExecute(aVoid);
        }
    }

    private class GetVcardForUser extends AsyncTask<String, Void, VCard> {
        private boolean success = false;
        private String jid = null;

        private View view = null;
        private int distance = 0;
        private Person person;

        public GetVcardForUser(View view, int distance, Person person) {
            this.view = view;
            this.distance = distance;
            this.person = person;
        }
        @Override
        protected VCard doInBackground(String... param) {
            XMPPTCPConnection connection = XMPPClient.getInstance().getConnection();
            VCard card = new VCard();
            try {
                jid = param[0];
                if (connection.isAuthenticated()) {
                    card.load(connection, jid + "@mm.io");
                    success = true;
                } else {
                    success = false;
                }
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            }
            return card;
        }
        @Override
        protected void onPostExecute(VCard vCard) {
            if (success) {
                onSuccessfulVcardRetrieval(view, vCard, jid, distance, person);
            } else {
                onUnSuccessfulVcardRetrieval(view, person);
            }
        }

    }

}

package com.sports.unity.messages.controller.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.NearByUserJsonCaller;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleAroundMeMap extends CustomAppCompatActivity {

    private static final String REQUEST_LISTENER_KEY = "nearby_key";
    private static final String REQUEST_TAG = "nearby_tag";
    private static final String SPORT_SELECTION_FOOTBALL = "football";
    private static final String SPORT_SELECTION_CRICKET = "cricket";


    private NearByUserJsonCaller nearByUserJsonCaller = new NearByUserJsonCaller();

    private GoogleMap map;
    private LatLng latLong;
    private String sportSelection = SPORT_SELECTION_FOOTBALL;

    private Toolbar toolbar;
    private TextView titleAddress;
    private TextView titleCity;

    private String base_url = "http://54.169.217.88/retrieve_nearby_users?lat=";
    private int radius = 500;

    private int stepRange = 25;

    private ProgressDialog dialog = null;

    private ArrayList<JSONObject> userList = null;

    private ScoresContentHandler.ContentListener contentListener = new ScoresContentHandler.ContentListener() {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if (tag.equals(REQUEST_TAG)) {
                if (responseCode == 200) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ArrayList<JSONObject> users = ScoresJsonParser.parseListOfNearByUsers(content);
                    userList = users;
                    if (users != null) {
                        plotMarkers(users, sportSelection);
                    } else {
                        //TODO
                    }

                } else {

                }
            } else {
                //nothing
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_around_me_map);

        initToolbar();
        openMap();
        InitSeekbar();
        setsportSelectionButtons();
        setCustomButtonsForNavigationAndUsers();
    }

    private void setCustomButtonsForNavigationAndUsers() {
        ImageView myLocation = (ImageView) findViewById(R.id.myLocation);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLocationIfGPSEnabled();
            }
        });
        ImageView refreshUsers = (ImageView) findViewById(R.id.refreshUsers);
        refreshUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPeopleAroundMe(latLong.latitude, latLong.longitude);
            }
        });
    }

    private void setsportSelectionButtons() {
        final Button cricket = (Button) findViewById(R.id.people_cricket_interest);
        final Button football = (Button) findViewById(R.id.people_football_interest);
        cricket.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        football.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        football.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.clear();
                LinearLayout sportSwitchLAyout = (LinearLayout) findViewById(R.id.switch_sports);
                sportSwitchLAyout.setBackground(getResources().getDrawable(R.drawable.btn_s1_focused));
                sportSelection = SPORT_SELECTION_FOOTBALL;
                football.setTextColor(Color.WHITE);
                cricket.setTextColor(getResources().getColor(R.color.app_theme_blue));
                plotMarkers(userList, sportSelection);
            }
        });

        cricket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.clear();
                LinearLayout sportSwitchLAyout = (LinearLayout) findViewById(R.id.switch_sports);
                sportSwitchLAyout.setBackground(getResources().getDrawable(R.drawable.btn_s2_focused));
                sportSelection = SPORT_SELECTION_CRICKET;
                cricket.setTextColor(Color.WHITE);
                football.setTextColor(getResources().getColor(R.color.app_theme_blue));
                plotMarkers(userList, sportSelection);
            }
        });
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
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_05));
                    map.animateCamera(CameraUpdateFactory.zoomTo(16));
                    radius = 500;
                    fetchUsersNearByWithNewRadius();
                } else if (progress >= 0 * stepRange + stepRange / 2 && progress <= 0 * stepRange + stepRange / 2 + stepRange) {
                    seekBar.setProgress(1 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_10));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14));
                    radius = 1000;
                    fetchUsersNearByWithNewRadius();
                } else if (progress >= 1 * stepRange + stepRange / 2 && progress <= 1 * stepRange + stepRange / 2 + stepRange) {
                    seekBar.setProgress(2 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_20));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14));
                    radius = 2000;
                    fetchUsersNearByWithNewRadius();
                } else if (progress >= 2 * stepRange + stepRange / 2 && progress <= 2 * stepRange + stepRange / 2 + stepRange) {
                    seekBar.setProgress(3 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_30));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14));
                    radius = 3000;
                    fetchUsersNearByWithNewRadius();
                } else {
                    seekBar.setProgress(4 * stepRange);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_distance_slider_40));
                    map.animateCamera(CameraUpdateFactory.zoomTo(12));
                    radius = 4000;
                    fetchUsersNearByWithNewRadius();
                }
            }
        });
    }

    private void fetchUsersNearByWithNewRadius() {
        getPeopleAroundMe(latLong.latitude, latLong.longitude);
    }

    private void plotMarkers(ArrayList<JSONObject> users, String sportSelection) {
//        nearByUserJsonCaller.setJsonObject(users.get(0));
//        nearByUserJsonCaller.getUsername();
//        nearByUserJsonCaller.getDistance();
        for (JSONObject user : users) {
            nearByUserJsonCaller.setJsonObject(user);
            try {
                String interests = nearByUserJsonCaller.getInterests();
                if (interests.contains(sportSelection)) {
                    double latitude = nearByUserJsonCaller.getLatitude();
                    double longitude = nearByUserJsonCaller.getLongitude();
                    MarkerOptions markerOption = new MarkerOptions().position(new LatLng(latitude, longitude));
                    if (sportSelection.equals(SPORT_SELECTION_FOOTBALL)) {
                        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mrkr_fball));
                    } else {
                        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mrkr_cri));
                    }
                    markerOption.snippet(nearByUserJsonCaller.getUsername());
                    map.addMarker(markerOption);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar_map);
        titleAddress = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleAddress.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        titleCity = (TextView) toolbar.findViewById(R.id.secondary_title);
        titleCity.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        setCurrentAddressOnToolbar(titleAddress, titleCity);

    }

    private void setCurrentAddressOnToolbar(TextView titleAddress, TextView titleCity) {
        titleAddress.setText(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_LOCATION));
        titleCity.setText(TinyDB.getInstance(getApplicationContext()).getString(TinyDB.KEY_ADDRESS_STATE));
    }

    private void openMap() {

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(false);
        double latitude = TinyDB.getInstance(getApplicationContext()).getDouble(TinyDB.KEY_CURRENT_LATITUDE, 0.0);
        double longitude = TinyDB.getInstance(getApplicationContext()).getDouble(TinyDB.KEY_CURRENT_LONGITUDE, 0.0);
        latLong = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 16));
        getPeopleAroundMe(latitude, longitude);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showProfile(marker);
                return false;
            }
        });
    }

    private void getPeopleAroundMe(double latitude, double longitude) {

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

        map.clear();
        dialog = ProgressDialog.show(PeopleAroundMeMap.this, "",
                "fetching...", true);
        dialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());

        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);

        String urlToRequest = base_url + latitude + "&lng=" + longitude + "&radius=" + radius;
        ScoresContentHandler.getInstance().requestNearByUsers(REQUEST_LISTENER_KEY, REQUEST_TAG, urlToRequest);
    }

    public boolean createContact(String number, Context context, VCard vCard) {
        boolean success = false;
        SportsUnityDBHelper.getInstance(context).addToContacts(vCard.getNickName(), number, true, ContactsHandler.getInstance().defaultStatus, false);
        SportsUnityDBHelper.getInstance(context).updateContacts(number, vCard.getAvatar(), vCard.getMiddleName());
        return success;
    }

    private void showProfile(final Marker marker) {
        LayoutInflater inflater = PeopleAroundMeMap.this.getLayoutInflater();
        View popupProfile = inflater.inflate(R.layout.chat_other_profile_layout, null);

        AlertDialog.Builder otherProfileBuilder = new AlertDialog.Builder(PeopleAroundMeMap.this);
        otherProfileBuilder.setView(popupProfile);

        AlertDialog nearByUserProfile = otherProfileBuilder.create();
        nearByUserProfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nearByUserProfile.show();

        popupProfile.findViewById(R.id.progressBarProfile).setVisibility(View.VISIBLE);

        new GetVcardForUser(popupProfile).execute(marker.getSnippet());
    }

    private void populateProfilePopup(final VCard vCard, View popupProfile, final String number) {
        popupProfile.findViewById(R.id.progressBarProfile).setVisibility(View.GONE);

        TextView name = (TextView) popupProfile.findViewById(R.id.username);
        name.setText(vCard.getNickName());
        name.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
        byte[] image = vCard.getAvatar();
        if (image != null) {
            CircleImageView imageview = (CircleImageView) popupProfile.findViewById(R.id.user_pic);
            imageview.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        } else {
            // nothing
        }
        TextView distance = (TextView) popupProfile.findViewById(R.id.distanceFromMe);
        distance.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoMedium());
        TextView sport = (TextView) popupProfile.findViewById(R.id.sport);
        sport.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        sport.setText(sportSelection);
        Button sayHello = (Button) popupProfile.findViewById(R.id.start_chat);
        sayHello.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        sayHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contacts contact = SportsUnityDBHelper.getInstance(getApplicationContext()).getContact(number);
                if (contact == null) {
                    createContact(number, getApplicationContext(), vCard);
                    contact = SportsUnityDBHelper.getInstance(getApplicationContext()).getContact(number);
                    moveToChatActivity(contact, false);
                } else {
                    moveToChatActivity(contact, true);
                }
            }
        });
    }

    private void moveToChatActivity(Contacts contact, boolean contactAvailable) {
        String number = contact.jid;
        String name = contact.name;
        long contactId = contact.id;
        byte[] userPicture = contact.image;

        String groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
        long chatId = SportsUnityDBHelper.getInstance(getApplicationContext()).getChatEntryID(contactId, groupServerId);
        if (chatId == SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            if (contactAvailable) {
                //nothing
            } else {
                chatId = SportsUnityDBHelper.getInstance(getApplicationContext()).createChatEntry(name, contactId, true);
            }

        }
        boolean blockStatus = SportsUnityDBHelper.getInstance(getApplicationContext()).isChatBlocked(contactId);

        Intent chatScreenIntent = new Intent(PeopleAroundMeMap.this, ChatScreenActivity.class);
        chatScreenIntent.putExtra("number", number);
        chatScreenIntent.putExtra("name", name);
        chatScreenIntent.putExtra("contactId", contactId);
        chatScreenIntent.putExtra("chatId", chatId);
        chatScreenIntent.putExtra("groupServerId", groupServerId);
        chatScreenIntent.putExtra("userpicture", userPicture);
        chatScreenIntent.putExtra("blockStatus", blockStatus);
        if (contactAvailable) {
            chatScreenIntent.putExtra("otherChat", false);
        } else {
            chatScreenIntent.putExtra("otherChat", true);
        }
        startActivity(chatScreenIntent);
    }

    private void fetchLocationIfGPSEnabled() {
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
            Toast.makeText(getApplicationContext(), "Please turn on your Gps settings", Toast.LENGTH_SHORT).show();
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        Location location = map.getMyLocation();
        if (location != null) {
            TinyDB.getInstance(getApplicationContext()).putDouble(TinyDB.KEY_CURRENT_LATITUDE, location.getLatitude());
            TinyDB.getInstance(getApplicationContext()).putDouble(TinyDB.KEY_CURRENT_LONGITUDE, location.getLongitude());
            LatLng defaultLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 16));
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    titleAddress.setText(address.getAddressLine(0));
                    titleCity.setText(address.getLocality());
                    saveAddress(address.getAddressLine(0), address.getLocality());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAddress(String street, String city) {
        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_ADDRESS_LOCATION, street);
        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_ADDRESS_STATE, city);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        ScoresContentHandler.getInstance().removeResponseListener(REQUEST_LISTENER_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_around_me_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetVcardForUser extends AsyncTask<String, Void, VCard> {
        private boolean success = false;
        private String number = null;

        private View view = null;

        public GetVcardForUser(View view) {
            this.view = view;
        }

        @Override
        protected VCard doInBackground(String... param) {
            XMPPTCPConnection connection = XMPPClient.getInstance().getConnection();
            VCard card = new VCard();
            try {
                number = param[0];
                card.load(connection, number + "@mm.io");
                success = true;
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
            return card;
        }

        @Override
        protected void onPostExecute(VCard vCard) {
            if (success) {
                onSuccessfulVcardRetrieval(view, vCard, number);
            } else {
                onUnSuccessfulVcardRetrieval();
            }
        }
    }

    private void onUnSuccessfulVcardRetrieval() {
        //TODO
    }

    private void onSuccessfulVcardRetrieval(View view, VCard vCard, String number) {
        populateProfilePopup(vCard, view, number);
    }


}

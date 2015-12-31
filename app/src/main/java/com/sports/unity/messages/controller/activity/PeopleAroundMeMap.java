package com.sports.unity.messages.controller.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.model.Contacts;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PeopleAroundMeMap extends CustomAppCompatActivity {

    private GoogleMap map;
    private LatLng latLong;

    private Toolbar toolbar;
    private TextView titleAddress;
    private TextView titleCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_around_me_map);

        initToolbar();
        openMap();
        InitSeekbar();
    }

    private void InitSeekbar() {
        SeekBar seekDistance = (SeekBar) findViewById(R.id.distance_seekbar);
        seekDistance.incrementProgressBy(1);
        seekDistance.setMax(4);
        seekDistance.setProgress(0);
        seekDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 1) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_audio_s));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14));
                } else if (progress == 2) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_camera_disabled));
                    map.animateCamera(CameraUpdateFactory.zoomTo(12));
                } else if (progress == 3) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_camera_pressed));
                    map.animateCamera(CameraUpdateFactory.zoomTo(10));
                } else if (progress == 4) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_camera_disabled));
                    map.animateCamera(CameraUpdateFactory.zoomTo(8));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void plotMarker() {
        MarkerOptions markerOption = new MarkerOptions().position(latLong);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user));
        map.addMarker(markerOption);
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar_map);
        titleAddress = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleCity = (TextView) toolbar.findViewById(R.id.secondary_title);
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
        plotMarker();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showProfile();
                return false;
            }
        });
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                checkIfGPSEnabled();
                return true;
            }
        });

    }

    public boolean createContact(String number, Context context) {
        boolean success = false;
        try {
            XMPPTCPConnection connection = XMPPClient.getInstance().getConnection();
            VCard card = new VCard();
            card.load(connection, number + "@mm.io");
            String status = card.getMiddleName();
            byte[] image = card.getAvatar();

            SportsUnityDBHelper.getInstance(context).addToContacts(number, number, true, ContactsHandler.getInstance().defaultStatus, false);
            SportsUnityDBHelper.getInstance(context).updateContacts(number, image, status);

            success = true;
        } catch (Throwable throwable) {

        }
        return success;
    }

    private void showProfile() {
        LayoutInflater inflater = PeopleAroundMeMap.this.getLayoutInflater();
        View popupProfile = inflater.inflate(R.layout.chat_other_profile_layout, null);
        AlertDialog.Builder otherProfile = new AlertDialog.Builder(PeopleAroundMeMap.this);
        otherProfile.setView(popupProfile);
        otherProfile.show();
        popupProfile.findViewById(R.id.start_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contacts contact = SportsUnityDBHelper.getInstance(getApplicationContext()).getContact("919953936440");
                if (contact == null) {
                    createContact("919953936440", getApplicationContext());
                    contact = SportsUnityDBHelper.getInstance(getApplicationContext()).getContact("919953936440");
                }
                moveToChatActivity(contact);
            }
        });
    }

    private void moveToChatActivity(Contacts contact) {
        String number = contact.jid;
        String name = contact.name;
        long contactId = contact.id;
        byte[] userPicture = contact.image;

        String groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
        long chatId = SportsUnityDBHelper.getInstance(getApplicationContext()).getChatEntryID(contactId, groupServerId);
        if (chatId == SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            chatId = SportsUnityDBHelper.getInstance(getApplicationContext()).createChatEntry(name, contactId, true);
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
        startActivity(chatScreenIntent);
    }

    private void checkIfGPSEnabled() {
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
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


}

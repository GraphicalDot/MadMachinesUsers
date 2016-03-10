package com.sports.unity.util.network;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.TinyDB;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Created by madmachines on 29/12/15.
 */
public class LocManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static String base_url = "http://" + XMPPClient.SERVER_HOST + "/set_location?user=";
    private static LocManager locManager = null;
    boolean dataSent = false;
    private Thread uploadLocation = null;
    private Thread getLocation = null;
    private String url = "";
    private Context context;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    public LocManager(Context context) {
        this.context = context;
    }

    synchronized public static LocManager getInstance(Context context) {
        if (locManager == null) {
            locManager = new LocManager(context);
        }
        return locManager;
    }

    public synchronized void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void connect() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void retrieveLocation() {
        if (getLocation != null && getLocation.isAlive()) {
            //do nothing
        } else {
            if (getLocation != null && getLocation.isAlive()) {
                //do nothing
            } else {
                getLocation = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        } catch (SecurityException ex) {
                            ex.printStackTrace();
                        }
                        if (mLastLocation != null) {
                            saveLocation(mLastLocation);
                        }
                    }
                });
                getLocation.start();
            }

        }
    }

    public Location getLocation() {
        if (mLastLocation == null) {
            retrieveLocation();
        }
        return mLastLocation;

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("fusedlocationapi", "connected");
        retrieveLocation();
    }

    public void sendLatituteAndLongitude(final Location mLastLocation, boolean forceSend) {
        if (uploadLocation != null && uploadLocation.isAlive()) {
            //do nothing
        } else {
            if (forceSend || dataSent == false) {
                uploadLocation = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadLatLng(mLastLocation);
                    }
                });
                uploadLocation.start();
            }
        }
    }

    public void uploadLatLng(Location mLastLocation) {
        HttpURLConnection httpURLConnection = null;
        url = base_url + TinyDB.getInstance(context).getString(TinyDB.KEY_USERNAME) + "@mm.io&lat=" + mLastLocation.getLatitude() + "&lng=" + mLastLocation.getLongitude();
        try {
            URL sendData = new URL(url);
            httpURLConnection = (HttpURLConnection) sendData.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setDoInput(false);
            httpURLConnection.setRequestMethod("GET");

            if (httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK) {
                dataSent = true;
                Log.i("data sent", "true");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
            }
        }
    }

    public void saveLocation(Location mLastLocation) {
        if (mLastLocation != null) {
            Log.i("latitude", String.valueOf(mLastLocation.getLatitude()));
            Log.i("longitude", String.valueOf(mLastLocation.getLongitude()));

            TinyDB.getInstance(context).putDouble(TinyDB.KEY_CURRENT_LATITUDE, mLastLocation.getLatitude());
            TinyDB.getInstance(context).putDouble(TinyDB.KEY_CURRENT_LONGITUDE, mLastLocation.getLongitude());
            saveStreetAddress(mLastLocation);


        }
    }

    private void saveStreetAddress(Location mLastLocation) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                TinyDB.getInstance(context).putString(TinyDB.KEY_ADDRESS_LOCATION, address.getAddressLine(0));
                TinyDB.getInstance(context).putString(TinyDB.KEY_ADDRESS_STATE, address.getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean ismGoogleApiClientConnected() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
        } else {
            return false;
        }
    }
}

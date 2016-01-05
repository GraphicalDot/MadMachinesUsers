package com.sports.unity.util.network;

import android.content.Context;
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
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by madmachines on 29/12/15.
 */
public class LocManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static LocManager locManager = null;
    private Thread uploadLocation = null;
    public static String base_url = "54.169.217.88/set_location?user=";
    String url = "";

    synchronized public static LocManager getInstance(Context context) {
        if (locManager == null) {
            locManager = new LocManager(context);
        }
        return locManager;
    }

    private Context context;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    public LocManager(Context context) {
        this.context = context;
    }

    public synchronized void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public Location getLocation() {
        if (mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            //TODO
        }
        return mLastLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("fusedlocationapi", "connected");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            saveLocation(mLastLocation);
            sendLatituteAndLongitude(mLastLocation);
        } else {
            //TODO
        }
    }

    private void sendLatituteAndLongitude(final Location mLastLocation) {
        if (uploadLocation != null && uploadLocation.isAlive()) {
            //do nothing
        } else {
            uploadLocation = new Thread(new Runnable() {
                @Override
                public void run() {
                    uploadLatLng(mLastLocation);
                }
            });
            uploadLocation.start();
        }

    }

    private void uploadLatLng(Location mLastLocation) {
        HttpURLConnection httpURLConnection = null;
        url = base_url + XMPPClient.getConnection().getUser() + "@mm.io&lat=" + mLastLocation.getLatitude() + "&lng=" + mLastLocation.getLongitude();
        try {
            URL sendData = new URL(url);
            httpURLConnection = (HttpURLConnection) sendData.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setDoInput(false);
            httpURLConnection.setRequestMethod("GET");
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i("latlongresponse", " 200 ");
            } else {
                Log.i("latlongresponse", " 500 ");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void saveLocation(Location mLastLocation) {
        if (mLastLocation != null) {
            Log.i("latitude", String.valueOf(mLastLocation.getLatitude()));
            Log.i("longitude", String.valueOf(mLastLocation.getLongitude()));

            TinyDB.getInstance(context).putDouble(TinyDB.KEY_CURRENT_LATITUDE, mLastLocation.getLatitude());
            TinyDB.getInstance(context).putDouble(TinyDB.KEY_CURRENT_LONGITUDE, mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

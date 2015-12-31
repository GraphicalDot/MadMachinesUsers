package com.sports.unity.util.network;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.sports.unity.common.model.TinyDB;

/**
 * Created by madmachines on 29/12/15.
 */
public class LocManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context context;

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    private static LocManager locManager = null;

    synchronized public static LocManager getInstance(Context context) {
        if (locManager == null) {
            locManager = new LocManager(context);
        }
        return locManager;
    }

    LocManager(Context context) {
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
        saveLocation(mLastLocation);
    }

    public void saveLocation(Location mLastLocation) {
        Log.i("latitude", String.valueOf(mLastLocation.getLatitude()));
        Log.i("longitude", String.valueOf(mLastLocation.getLongitude()));
        TinyDB.getInstance(context).putDouble(TinyDB.KEY_CURRENT_LATITUDE, mLastLocation.getLatitude());
        TinyDB.getInstance(context).putDouble(TinyDB.KEY_CURRENT_LONGITUDE, mLastLocation.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

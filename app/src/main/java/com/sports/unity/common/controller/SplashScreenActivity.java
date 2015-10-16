package com.sports.unity.common.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.SystemUiHider;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sports.unity.R.layout.activity_splash);

        if ( isUserRegisteredFromSharedPreference() ) {
            moveToNextActivity(MainActivity.class);
        } else {
            initViews();
            new fetch().execute();
        }

    }

    private boolean isUserRegisteredFromSharedPreference(){
        return TinyDB.getInstance(this).getBoolean( TinyDB.KEY_REGISTERED, false);
    }

    private void moveToNextActivity( Class nextActivityClass){
        Intent intent = new Intent(SplashScreenActivity.this, nextActivityClass);
        startActivity(intent);

        finish();
    }

    private void initViews() {
        findViewById(com.sports.unity.R.id.dummy_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                moveToNextActivity(EnterPhoneActivity.class);
            }

        });
    }

    public class fetch extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Log.i("contacts : ", "fetching");
            ContactsHandler.getInstance().getAllContacts(SplashScreenActivity.this);
            return null;
        }
    }

}

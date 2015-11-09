package com.sports.unity.common.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.crittercism.app.Crittercism;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.SystemUiHider;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sports.unity.R.layout.activity_splash);

        Crittercism.initialize(getApplicationContext(), "564059fcd224ac0a00ed42a3");

        UserUtil.init(this);

        if (UserUtil.isUserRegistered()) {
            if (UserUtil.isProfileCreated()) {
                if (UserUtil.isSportsSelected()) {
                    moveToNextActivity(MainActivity.class);
                } else {
                    moveToNextActivity(SelectSportsActivity.class);
                }
            } else {
                moveToNextActivity(ProfileCreationActivity.class);
            }
        } else {
            if( UserUtil.isOtpSent() ){
                moveToNextActivity(EnterOtpActivity.class);
            } else {
                show();
            }
        }

    }

    private void moveToNextActivity(Class nextActivityClass) {
        Intent mainIntent = new Intent(SplashScreenActivity.this, nextActivityClass);
        startActivity(mainIntent);
        finish();
    }

    private void show() {
        initViews();
        new fetch().execute();
    }

    private void initViews() {
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.animate()
                .setStartDelay(1000)
                .setDuration(2000)
                .scaleX(-50)
                .scaleY(-50);
//        img.animate()
//                .setStartDelay(1500)
//                .setDuration(2000)
//                .scaleX(20)
//                .scaleY(20);
        ImageView img1 = (ImageView) findViewById(R.id.imageView1);
        img1.animate().setStartDelay(1000)
                .setDuration(1800).alpha(-1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToNextActivity(TourActivity.class);
                overridePendingTransition(R.anim.f1, R.anim.f2);
            }
        }, 2000);
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

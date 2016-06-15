package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sports.unity.ChatScreenApplication;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;

import static com.sports.unity.BuildConfig.CRITTERCISM_API_KEY;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sports.unity.R.layout.activity_splash);

        Crittercism.initialize(getApplicationContext(), CRITTERCISM_API_KEY);
        UserUtil.init(this);

        {
            ChatScreenApplication application = (ChatScreenApplication) getApplication();
            Tracker mTracker = application.getDefaultTracker();
            mTracker.setScreenName("AppLaunchScreen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

//        cachedFlow( "8750825720", "641970");
//        showScreenSize();

        if (UserUtil.isUserRegistered()) {
            if (UserUtil.isProfileCreated()) {
                if (UserUtil.isSportsSelected()) {
                    if (UserUtil.isFilterCompleted()) {
                        moveToNextActivity(MainActivity.class);
                    } else {
                        moveToNextActivity(SelectSportsActivity.class);
                    }
                } else {
                    moveToNextActivity(SelectSportsActivity.class);
                }
            } else {
                moveToNextActivity(ProfileCreationActivity.class);
            }
        } else {
            if (UserUtil.isOtpSent()) {
                moveToNextActivity(EnterOtpActivity.class);
            } else {
                if (!UserUtil.isFilterCompleted()) {
                    show();
                } else {
                    moveToNextActivity(EnterPhoneActivity.class);
                }
            }
        }

    }

    private void cachedFlow(String phoneNumber, String password) {
        Context context = getApplicationContext();

        TinyDB.getInstance(context).putString(TinyDB.KEY_USERNAME, "91" + phoneNumber);
        UserUtil.setOtpSent(getBaseContext(), true);

        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_PASSWORD, password);
        UserUtil.setOtpSent(context, false);
        UserUtil.setUserRegistered(context, true);

        ContactsHandler.getInstance().addCallToSyncContacts(context);
    }

    private void showScreenSize() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String toastMsg;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                toastMsg = "XLarge screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = "Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = "Normal screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                toastMsg = "Small screen";
                break;
            default:
                toastMsg = "Screen size is neither large, normal or small";
        }

        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }

    private void moveToNextActivity(Class nextActivityClass) {
        Intent mainIntent = new Intent(SplashScreenActivity.this, nextActivityClass);
        startActivity(mainIntent);
        finish();
    }

    private void show() {
        initViews();
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

}

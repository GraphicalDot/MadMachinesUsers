package com.sports.unity.common.controller;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

public class About extends AppCompatActivity {

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.back_button) {
                onBackPressed();
            } else if (v.getId() == R.id.terms) {
                CommonUtil.openLinkOnBrowser(About.this, getResources().getString(R.string.link_of_terms_of_use));
            } else if (v.getId() == R.id.privacy_policy) {
                CommonUtil.openLinkOnBrowser(About.this, getResources().getString(R.string.link_of_privacy_policy));
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initToolbar();
        initCustomFonts();

    }

    private void initCustomFonts() {
        TextView sportsUnityClub = (TextView) findViewById(R.id.sportsunity_club);
        TextView terms = (TextView) findViewById(R.id.terms);
        TextView privacyPolicy = (TextView) findViewById(R.id.privacy_policy);
        TextView copyright = (TextView) findViewById(R.id.copyright);
        TextView version = (TextView) findViewById(R.id.version);
        TextView versionNumber = (TextView) findViewById(R.id.version_int);

        copyright.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        version.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        versionNumber.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        sportsUnityClub.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        terms.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        privacyPolicy.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionNumber.setText(pInfo.versionName);

        terms.setOnClickListener(onClickListener);
        terms.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

        privacyPolicy.setOnClickListener(onClickListener);
        privacyPolicy.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, false));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView backButton = (ImageView) toolbar.findViewById(R.id.back_button);
        backButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        backButton.setOnClickListener(onClickListener);
    }

}

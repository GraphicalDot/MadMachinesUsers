package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;


public class TourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        initViews();
    }

    private void initViews(){
        int noOfTabs = 3;
        //Set the pager with an adapter
        TourAdapter adapter = new TourAdapter(getSupportFragmentManager(), noOfTabs);
        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(com.sports.unity.R.id.pager);

        final Button btn = (Button) findViewById(R.id.btn);
        btn.setText("Skip");
        btn.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveOnToNextActivity();
            }
        });

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //nothing
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.radioButton);
                        btn.setText("Skip");

                        break;
                    case 1:
                        radioGroup.check(R.id.radioButton2);
                        btn.setText("Skip");
                        break;
                    case 2:
                        radioGroup.check(R.id.radioButton3);
                        btn.setText("Continue");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //nothing
            }

        });
        pager.setAdapter(adapter);
    }

    private void moveOnToNextActivity(){
        Intent intent = new Intent(TourActivity.this, EnterPhoneActivity.class);
        startActivity(intent);
        finish();
    }

}

package com.sports.unity.scoredetails.footballdetail;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.SlidingTabLayout;

import java.util.HashMap;

public class FootballDetailsActivity extends CustomVolleyCallerActivity {

    private  SlidingTabLayout slidingTabLayout;
    private  TextView venueName;
    private  TextView matchDate;
    private  TextView firstTeamName;
    private  TextView matchTime;
    private  TextView matchDay;
    private  TextView secondTeamName;
    private ImageView teamFirstImage;
    private ImageView teamSecondImage;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_deatils);

        String leagueID = "1005";
    }
    /*This method use to set property of this activity*/
    private void intiActivityElements(){

    }
    /*This method use to get data from API*/
    private void dataInit(final String leagueID, String requestListenerKey,
                          String requestTag){
        HashMap<String,String> parameters = new HashMap<>();

    }

}

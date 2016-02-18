package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.scoredetails.CommentaryFragment;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.cricketdetail.CricketLiveMatchSummaryFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketMatchSummaryFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketPlayerBioFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketPlayerMachStatFragment;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by madmachines on 17/2/16.
 */
public class CricketPlayerProfileAdapter extends FragmentStatePagerAdapter {


    private String titles[];
    private int numberOfTabs;
    public CricketPlayerProfileAdapter(FragmentManager fm, String[] titles, int numberOfTabs) {
        super(fm);
        this.titles = titles;
        this.numberOfTabs = numberOfTabs;
     }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0) {
            fragment = new CricketPlayerBioFragment();
        } else {
            fragment = new CricketPlayerMachStatFragment();

        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}

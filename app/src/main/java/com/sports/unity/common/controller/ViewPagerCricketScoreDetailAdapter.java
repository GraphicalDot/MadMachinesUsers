package com.sports.unity.common.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.commentary.MatchCommentaryFragment;
import com.sports.unity.scoredetails.cricketdetail.CompletedMatchScoreCardFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketLiveMatchSummaryFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketUpcomingMatchScoreCardFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketUpcomingMatchSummaryFragment;
import com.sports.unity.scoredetails.cricketdetail.LiveCricketMatchScoreCardFragment;

import java.util.ArrayList;

/**
 * Created by cfeindia on 3/2/16.
 */
public class ViewPagerCricketScoreDetailAdapter extends FragmentStatePagerAdapter {
    private String titles[];
    private int numberOfTabs;
    private ArrayList<CommentriesModel> commentries;
    private String matchStatus;

    public ViewPagerCricketScoreDetailAdapter(FragmentManager fm, String[] titles, int numberOfTabs, ArrayList<CommentriesModel> commentries, String matchStatus) {
        super(fm);
        this.titles = titles;
        this.numberOfTabs = numberOfTabs;
        this.commentries = commentries;
        this.matchStatus = matchStatus;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0) {
            if(matchStatus.equalsIgnoreCase("N")|| matchStatus.trim().equalsIgnoreCase("")){
                fragment = new CricketUpcomingMatchSummaryFragment();
            }else if(matchStatus.equalsIgnoreCase("L")){
                fragment = new CricketLiveMatchSummaryFragment();
            } else  {
//                fragment = new CricketCompletedMatchSummaryFragment("");
            }
           // fragment = new CricketLiveMatchSummaryFragment();
        } else if (position == 1) {

                fragment = new MatchCommentaryFragment();

        } else {
            if(matchStatus.equalsIgnoreCase("N") || matchStatus.trim().equalsIgnoreCase("")){
                fragment = new CricketUpcomingMatchScoreCardFragment();
            }else if(matchStatus.equalsIgnoreCase("L")){
                fragment = new LiveCricketMatchScoreCardFragment();}
            else {
                fragment = new CompletedMatchScoreCardFragment();
            }
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

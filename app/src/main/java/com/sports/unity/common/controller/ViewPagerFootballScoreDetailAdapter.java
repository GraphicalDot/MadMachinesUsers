package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.scoredetails.CommentaryFragment;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.footballdetail.CompletedFootballMatchLineUpFragment;
import com.sports.unity.scoredetails.footballdetail.CompletedFootballMatchStatFragment;
import com.sports.unity.scoredetails.footballdetail.CompletedFootballMatchTimeLineFragment;
import com.sports.unity.scoredetails.footballdetail.LiveFootballMatchLineUpFargment;
import com.sports.unity.scoredetails.footballdetail.LiveFootballMatchStatFragment;
import com.sports.unity.scoredetails.footballdetail.LiveFootballMatchTimeLineFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchFromFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchSqadFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchTableFargment;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import java.util.ArrayList;


public class ViewPagerFootballScoreDetailAdapter extends FragmentStatePagerAdapter {


    private String Titles[];
    private int numberOfTabs;
    private ArrayList<CommentriesModel> commentries;
    private String matchStatus;
    private String matchTime;
    private boolean isLive;



    public ViewPagerFootballScoreDetailAdapter(FragmentManager fm, String mTitles[], int numberOfTabs, ArrayList<CommentriesModel> commentries,String matchStatus,String matchTime,boolean isLive) {
        super(fm);

        this.Titles = mTitles;
        this.numberOfTabs = numberOfTabs;
        this.commentries = commentries;
        this.matchStatus = matchStatus;
        this.matchTime = matchTime;
        this.isLive = isLive;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if(matchStatus.equals(matchTime) || "Postp.".equalsIgnoreCase(matchStatus) && !isLive){
            if (position == 0) {
                fragment = new UpCommingFootballMatchTableFargment();
            } else if (position == 1) {
                fragment = new UpCommingFootballMatchFromFragment();
            } else if(position == 2) {
                fragment = new UpCommingFootballMatchSqadFragment();
            }
        }else {
            if (position == 0) {
                fragment = new CommentaryFragment();
                Bundle cmBundel = new Bundle();
                cmBundel.putString(Constants.INTENT_KEY_TYPE, ScoresJsonParser.FOOTBALL);
                cmBundel.putParcelableArrayList("commentries", commentries);
                fragment.setArguments(cmBundel);
            } else if (position == 1) {
                if(isLive){
                    fragment = new LiveFootballMatchStatFragment();
                }else {
                    fragment = new CompletedFootballMatchStatFragment();
                }

            } else if(position == 2){
                if(isLive){
                    fragment = new LiveFootballMatchTimeLineFragment();
                }else {
                    fragment = new CompletedFootballMatchTimeLineFragment();
                }
            } else {
                if(isLive){
                    fragment = new LiveFootballMatchLineUpFargment();
                }else {
                    fragment = new CompletedFootballMatchLineUpFragment();
                }
            }
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}

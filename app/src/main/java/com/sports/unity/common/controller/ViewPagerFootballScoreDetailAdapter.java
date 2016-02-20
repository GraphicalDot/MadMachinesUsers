package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.messages.controller.fragment.MessagesFragment;
import com.sports.unity.news.controller.fragment.NewsFragment;
import com.sports.unity.scoredetails.CommentaryFragment;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.footballdetail.FootballMatchDetailFragment;
import com.sports.unity.scoredetails.footballdetail.FootballMatchLineupFragment;
import com.sports.unity.scoredetails.footballdetail.FootballMatchLineupModel;
import com.sports.unity.scoredetails.footballdetail.FootballMatchStatsFragment;
import com.sports.unity.scoredetails.footballdetail.FootballMatchTimelineFragment;
import com.sports.unity.scores.controller.fragment.MatchListFragment;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import java.util.ArrayList;


public class ViewPagerFootballScoreDetailAdapter extends FragmentStatePagerAdapter {


    private String Titles[];
    private int numberOfTabs;
    private ArrayList<CommentriesModel> commentries;
    private String matchStatus;



    public ViewPagerFootballScoreDetailAdapter(FragmentManager fm, String mTitles[], int numberOfTabs, ArrayList<CommentriesModel> commentries,String matchStatus) {
        super(fm);

        this.Titles = mTitles;
        this.numberOfTabs = numberOfTabs;
        this.commentries = commentries;
        this.matchStatus = matchStatus;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0) {
            fragment = new CommentaryFragment();
            Bundle cmBundel = new Bundle();
            cmBundel.putString(Constants.INTENT_KEY_TYPE, ScoresJsonParser.FOOTBALL);
            cmBundel.putParcelableArrayList("commentries", commentries);
            fragment.setArguments(cmBundel);
        } else if (position == 1) {
            fragment= new FootballMatchStatsFragment();
        } else if(position == 2){
            fragment = new FootballMatchTimelineFragment();
        } else {
            fragment = new FootballMatchLineupFragment();
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

package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.messages.controller.fragment.MessagesFragment;
import com.sports.unity.news.controller.fragment.NewsFragment;
import com.sports.unity.scoredetails.CommentaryFragment;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.cricketdetail.CricketMatchDetailFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketMatchScoreFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketMatchSummaryFragment;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cfeindia on 3/2/16.
 */
public class ViewPagerCricketScoreDetailAdapter extends FragmentStatePagerAdapter {


    private String titles[];
    private int numberOfTabs;
    private ArrayList<CommentriesModel> commentries;


    public ViewPagerCricketScoreDetailAdapter(FragmentManager fm, String[] titles, int numberOfTabs, ArrayList<CommentriesModel> commentries) {
        super(fm);
        this.titles = titles;
        this.numberOfTabs = numberOfTabs;
        this.commentries = commentries;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0) {
            fragment = new CommentaryFragment();
            Bundle cmBundel = new Bundle();
            cmBundel.putString(Constants.INTENT_KEY_TYPE, ScoresJsonParser.CRICKET);
           // cmBundel.putString(Constants.INTENT_KEY_ID,ScoresJsonParser.CRICKET);
            cmBundel.putParcelableArrayList("commentries", commentries);
            fragment.setArguments(cmBundel);
        } else if (position == 1) {
            fragment = new CricketMatchSummaryFragment();
        } else {
            fragment = new CricketMatchScoreFragment();

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

package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.messages.controller.fragment.MessagesFragment;
import com.sports.unity.news.controller.fragment.NewsFragment;
import com.sports.unity.scores.controller.fragment.MatchListFragment;
import com.sports.unity.util.Constants;

/**
 * Created by cfeindia on 3/2/16.
 */
public class ViewPagerFootballScoreDetailAdapter extends FragmentStatePagerAdapter {


    private String Titles[];
    private int numberOfTabs;


    public ViewPagerFootballScoreDetailAdapter(FragmentManager fm, String mTitles[], int numberOfTabs) {
        super(fm);

        this.Titles = mTitles;
        this.numberOfTabs = numberOfTabs;

    }

    @Override
    public Fragment getItem(int position) {


        if (position == 0) {
            MatchListFragment matchesFragment = new MatchListFragment();
            return matchesFragment;
        } else if (position == 1) {
            NewsFragment newsFragment = new NewsFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.INTENT_KEY_SEARCH_ON, false);
            newsFragment.setArguments(bundle);
            return newsFragment;
        } else {
            MessagesFragment messagesFragment = new MessagesFragment();
            return messagesFragment;
        }

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

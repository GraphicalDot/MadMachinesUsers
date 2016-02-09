package com.sports.unity.common.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.common.controller.fragment.FilterFragment;
import com.sports.unity.common.controller.fragment.GroupsFragment;
import com.sports.unity.common.controller.fragment.IntrestsFragment;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

/**
 * Created by madmachines on 27/1/16.
 */
public class ViewPagerAdapterForFilter extends FragmentStatePagerAdapter {

    private String Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapterInMainActivity is created
    private int numberOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapterInMainActivity is created
    private String[] titleFootball = {Constants.FILTER_TYPE_TEAM, Constants.FILTER_TYPE_LEAGUE, Constants.FILTER_TYPE_PLAYER};
    private String[] titleCricket = {Constants.FILTER_TYPE_TEAM, Constants.FILTER_TYPE_PLAYER};

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterForFilter(FragmentManager fm) {
        super(fm);
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {

            this.Titles = titleFootball;
            this.numberOfTabs = 3;
        } else {
            this.Titles = titleCricket;
            this.numberOfTabs = 2;
        }

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {


        if (position == 0) // if the position is 0 we are returning the First tab
        {
            FilterFragment filterFragment = new FilterFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
            filterFragment.setArguments(bundle);
            return filterFragment;
        } else if (position == 1) {
            if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
                FilterFragment filterFragment = new FilterFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_LEAGUE);
                filterFragment.setArguments(bundle);
                return filterFragment;
            } else {
                FilterFragment filterFragment = new FilterFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_PLAYER);
                filterFragment.setArguments(bundle);
                return filterFragment;
            }
        } else if (position == 2) {
            FilterFragment filterFragment = new FilterFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_PLAYER);
            filterFragment.setArguments(bundle);
            return filterFragment;
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return numberOfTabs;
    }

}

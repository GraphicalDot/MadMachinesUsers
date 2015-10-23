package com.sports.unity.common.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.sports.unity.common.controller.fragment.BasketballFragment;
import com.sports.unity.common.controller.fragment.CricketFragment;
import com.sports.unity.common.controller.fragment.FootballFragment;
import com.sports.unity.common.controller.fragment.FormullaOneFragment;
import com.sports.unity.common.controller.fragment.TennisFragment;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by madmachines on 29/9/15.
 */
public class ViewPagerAdapter1 extends FragmentStatePagerAdapter {

    ArrayList<String> Titles=new ArrayList<String>(); // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter1(FragmentManager fm, ArrayList<String> mTitles, int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }


    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

     String sport=Titles.get(position);
        if(sport.equals(Constants.GAME_KEY_CRICKET)) {
            return new CricketFragment();
        } else if(sport.equals(Constants.GAME_KEY_FOOTBALL)) {
           return new FootballFragment();
        } else if(sport.equals(Constants.GAME_KEY_BASKETBALL)) {
            return new BasketballFragment();
        } else if(sport.equals(Constants.GAME_KEY_TENNIS)) {
            return new TennisFragment();
        } else {
            return new FormullaOneFragment();
        }
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles.get(position);
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}

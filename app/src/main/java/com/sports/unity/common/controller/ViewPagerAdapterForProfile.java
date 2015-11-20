package com.sports.unity.common.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.common.controller.fragment.GroupsFragment;
import com.sports.unity.common.controller.fragment.IntrestsFragment;
import com.sports.unity.messages.controller.fragment.MessagesFragment;
import com.sports.unity.news.controller.fragment.NewsFragment;
import com.sports.unity.scores.controller.fragment.ScoresFragment;

/**
 * Created by madmachines on 18/11/15.
 */
public class ViewPagerAdapterForProfile extends FragmentStatePagerAdapter {

    private String Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int numberOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterForProfile(FragmentManager fm, String mTitles[], int numberOfTabs) {
        super(fm);

        this.Titles = mTitles;
        this.numberOfTabs = numberOfTabs;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {


        if (position == 0) // if the position is 0 we are returning the First tab
        {
            GroupsFragment groupsFragment = new GroupsFragment();
            return groupsFragment;
        } else {
            IntrestsFragment intrestsFragment = new IntrestsFragment();
            return intrestsFragment;
        }

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



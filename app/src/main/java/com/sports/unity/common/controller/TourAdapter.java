package com.sports.unity.common.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by madmachines on 15/10/15.
 */
public class TourAdapter extends FragmentStatePagerAdapter {

    private int numbOfTabs;

    public TourAdapter(FragmentManager fm, int noOfTabs) {
        super(fm);
        this.numbOfTabs = noOfTabs;
    }

    @Override
    public Fragment getItem(int position) {


        if (position == 0) // if the position is 0 we are returning the First tab
        {
            Tour_News_Fragment news = new Tour_News_Fragment();
            return news;
        } else if (position == 1)            // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            Tour_Score_Fragment score = new Tour_Score_Fragment();
            return score;
        } else {
            Tour_People_Fragment people = new Tour_People_Fragment();
            return people;
        }

    }

    @Override
    public int getCount() {
        return numbOfTabs;
    }
}

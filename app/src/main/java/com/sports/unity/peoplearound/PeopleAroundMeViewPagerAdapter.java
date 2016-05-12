package com.sports.unity.peoplearound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeViewPagerAdapter extends FragmentStatePagerAdapter {


    private String titles[];
    private int numberOfTabs;


    public PeopleAroundMeViewPagerAdapter(FragmentManager fm, String[] titles, int numberOfTabs) {
        super(fm);

        this.titles = titles;
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;

        if (position == 0) {
            fragment = new PeopleAroundMeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PeopleAroundActivity.BUNDLE_TAG, PeopleAroundActivity.FRIENDS_KEY);
            fragment.setArguments(bundle);
        } else if (position == 1) {
            fragment = new PeopleAroundMeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PeopleAroundActivity.BUNDLE_TAG, PeopleAroundActivity.SPU_KEY);
            fragment.setArguments(bundle);
        } else {
            fragment = new PeopleAroundMeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PeopleAroundActivity.BUNDLE_TAG, PeopleAroundActivity.SIMILAR_USERS_KEY);
            fragment.setArguments(bundle);
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


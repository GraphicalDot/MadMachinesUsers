package com.sports.unity.common.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by Mad on 12/28/2015.
 */
public class FilterPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> sportsSelected;
    private String SPORTS_FILTER_TYPE;

    public FilterPagerAdapter(FragmentManager fm, ArrayList<String> sportsSelected, String sportsFilterType) {
        super(fm);
        this.sportsSelected = sportsSelected;
        this.SPORTS_FILTER_TYPE = sportsFilterType;
    }

    @Override
    public Fragment getItem(int position) {
        if (SPORTS_FILTER_TYPE.equals(Constants.FILTER_TYPE_LEAGUE)) {
            FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SPORTS_FILTER_TYPE, SPORTS_FILTER_TYPE);
            bundle.putString(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
            filterByTagFragment.setArguments(bundle);
            return filterByTagFragment;
        } else if (position == 0) {

            FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SPORTS_FILTER_TYPE, SPORTS_FILTER_TYPE);
            bundle.putString(Constants.SPORTS_TYPE, sportsSelected.get(0));
            filterByTagFragment.setArguments(bundle);
            return filterByTagFragment;
        } else {
            FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SPORTS_FILTER_TYPE, SPORTS_FILTER_TYPE);
            bundle.putString(Constants.SPORTS_TYPE, sportsSelected.get(1));
            filterByTagFragment.setArguments(bundle);
            return filterByTagFragment;
        }
    }

    @Override
    public int getCount() {
        if (SPORTS_FILTER_TYPE.equals(Constants.FILTER_TYPE_LEAGUE)) {
            return 1;
        } else {
            return sportsSelected.size();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (SPORTS_FILTER_TYPE.equals(Constants.FILTER_TYPE_LEAGUE)) {
            return Constants.GAME_KEY_FOOTBALL;
        } else {
            return sportsSelected.get(position);
        }
    }
}

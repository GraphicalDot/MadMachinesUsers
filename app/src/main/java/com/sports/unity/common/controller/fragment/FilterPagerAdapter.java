package com.sports.unity.common.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by Mad on 12/28/2015.
 */
public class FilterPagerAdapter extends FragmentStatePagerAdapter {
    private String SPORTS_TYPE;
    private String[] titleFootball = {Constants.FILTER_TYPE_LEAGUE, Constants.FILTER_TYPE_TEAM, Constants.FILTER_TYPE_PLAYER};
    private String[] titleCricket = {Constants.FILTER_TYPE_TEAM, Constants.FILTER_TYPE_PLAYER};
    private boolean singleUse = false;
    private String filterType;

    public FilterPagerAdapter(FragmentManager fm, String sportsType) {
        super(fm);
        this.SPORTS_TYPE = sportsType;
        singleUse = false;
    }

    public FilterPagerAdapter(FragmentManager fm, String sportsType, String filterType, boolean singleUse) {
        super(fm);
        this.SPORTS_TYPE = sportsType;
        this.singleUse = singleUse;
        this.filterType = filterType;
    }

    @Override
    public Fragment getItem(int position) {
        if (singleUse) {
            FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SPORTS_FILTER_TYPE, filterType);
            bundle.putString(Constants.SPORTS_TYPE, SPORTS_TYPE);
            filterByTagFragment.setArguments(bundle);
            return filterByTagFragment;
        } else  if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
           if (position == 0) {
                FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_LEAGUE);
                bundle.putString(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
                filterByTagFragment.setArguments(bundle);
                return filterByTagFragment;
            } else if (position == 1) {

                FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
                bundle.putString(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
                filterByTagFragment.setArguments(bundle);
                return filterByTagFragment;
            } else {
                FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_PLAYER);
                bundle.putString(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
                filterByTagFragment.setArguments(bundle);
                return filterByTagFragment;
            }
        } else if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_CRICKET)) {

            if (position == 0) {
                FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
                bundle.putString(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_CRICKET);
                filterByTagFragment.setArguments(bundle);
                return filterByTagFragment;
            } else if (position == 1) {

                FilterByTagFragment filterByTagFragment = new FilterByTagFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_PLAYER);
                bundle.putString(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_CRICKET);
                filterByTagFragment.setArguments(bundle);
                return filterByTagFragment;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        if(singleUse){
            return 1;
        }
        else if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
            return 3;
        } else if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_CRICKET)) {
            return 2;
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(singleUse){
            return filterType;
        }
        else if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
            return titleFootball[position];
        } else if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_CRICKET)) {
            return titleCricket[position];
        }
        return null;
    }
}

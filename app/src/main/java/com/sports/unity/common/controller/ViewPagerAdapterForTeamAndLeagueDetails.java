package com.sports.unity.common.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.viewhelper.GenericVolleyRequestResponseFragment;
import com.sports.unity.news.controller.fragment.NewsFragment;
import com.sports.unity.scoredetails.StaffPickedFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchSqadFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchTableFargment;
import com.sports.unity.scores.controller.fragment.MatchListFragment;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import java.util.HashMap;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ViewPagerAdapterForTeamAndLeagueDetails extends FragmentStatePagerAdapter {

    private String Titles[];
    private int numberOfTabs;
    private boolean enabled = true;
    private FavouriteItem favouriteItem;
    private String[] footballLeagueTitle = {"News", "Fixtures", "Table"};
    private String[] footballTeamTitle = {"News", "Fixtures", "Squad"};
    private String[] cricketTeamTitle = {"News", "Fixtures", "Squad"};
    private boolean isStaffPicked;

    public ViewPagerAdapterForTeamAndLeagueDetails(FragmentManager fm, FavouriteItem f, boolean isStaffPicked) {
        super(fm);
//        this.Titles = titles;
//        this.numberOfTabs = 4;
        favouriteItem = f;
        this.isStaffPicked = isStaffPicked;
        if (f.getFilterType().equalsIgnoreCase(Constants.FILTER_TYPE_LEAGUE) || isStaffPicked) {
            this.Titles = footballLeagueTitle;
            this.numberOfTabs = footballLeagueTitle.length;
        } else {
            if (f.getSportsType().equalsIgnoreCase(Constants.SPORTS_TYPE_CRICKET)) {
                this.Titles = cricketTeamTitle;
                this.numberOfTabs = cricketTeamTitle.length;
            } else if (f.getSportsType().equalsIgnoreCase(Constants.SPORTS_TYPE_FOOTBALL)) {
                this.Titles = footballTeamTitle;
                this.numberOfTabs = footballTeamTitle.length;
            }
        }

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) // if the position is 0 we are returning the First tab
        {
            fragment = new NewsFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.INTENT_KEY_SEARCH_ON, true);
            bundle.putString("search_keyword", favouriteItem.getName());
            fragment.setArguments(bundle);

        } else if (position == 1) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.INTENT_KEY_ID, favouriteItem.getJsonObject().toString());
            bundle.putBoolean(Constants.SPORTS_TYPE_STAFF, isStaffPicked);
            fragment = new MatchListFragment();
            fragment.setArguments(bundle);

        } else if (position == 2) {
            if (isStaffPicked && favouriteItem.getSportsType().equals(Constants.SPORTS_TYPE_CRICKET)) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.INTENT_KEY_ID, favouriteItem.getJsonObject().toString());

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put(ScoresContentHandler.PARAM_SERIESID, favouriteItem.getId());

                StaffPickedFragment helper = new StaffPickedFragment(null);
                helper.setRequestParameters(parameters);

                GenericVolleyRequestResponseFragment volleyFragment = new GenericVolleyRequestResponseFragment();
                volleyFragment.setBasicVolleyRequestResponseViewHelper(helper);
                fragment = volleyFragment;

//                fragment = new StaffPickedFragment();
//                fragment.setArguments(bundle);
            } else if (favouriteItem.getFilterType().equals(Constants.FILTER_TYPE_LEAGUE)) {
//                Bundle b = new Bundle();
//                b.putString(Constants.INTENT_KEY_LEAGUE_ID, favouriteItem.getId());

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put(ScoresContentHandler.PARAM_SERIESID, favouriteItem.getId());

                UpCommingFootballMatchTableFargment helper = new UpCommingFootballMatchTableFargment(null);
                helper.setRequestParameters(parameters);

                GenericVolleyRequestResponseFragment volleyFragment = new GenericVolleyRequestResponseFragment();
                volleyFragment.setBasicVolleyRequestResponseViewHelper(helper);
                fragment = volleyFragment;

//                fragment = new UpCommingFootballMatchTableFargment();
//                fragment.setArguments(b);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.INTENT_KEY_TEAM1_ID, favouriteItem.getJsonObject().toString());

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put(ScoresContentHandler.PARAM_TEAM_1, favouriteItem.getId());

                UpCommingFootballMatchSqadFragment helper = new UpCommingFootballMatchSqadFragment(null, null, bundle);
                helper.setRequestParameters(parameters);

                GenericVolleyRequestResponseFragment volleyFragment = new GenericVolleyRequestResponseFragment();
                volleyFragment.setBasicVolleyRequestResponseViewHelper(helper);
                fragment = volleyFragment;

//                fragment = new UpCommingFootballMatchSqadFragment();
//                fragment.setArguments(bundle);
            }

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

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
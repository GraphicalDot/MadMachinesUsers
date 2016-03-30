package com.sports.unity.common.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.sports.unity.R;
import com.sports.unity.common.controller.fragment.FilterFragment;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.fragment.MessagesFragment;
import com.sports.unity.news.controller.fragment.NewsFragment;
import com.sports.unity.news.model.NewsContentHandler;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchSqadFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchTableFargment;
import com.sports.unity.scores.controller.fragment.MatchListFragment;
import com.sports.unity.util.Constants;

import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_ID;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ViewPagerAdapterForTeamAndLeagueDetails extends FragmentStatePagerAdapter {

    private String Titles[];
    private int numberOfTabs;
    private boolean enabled = true;
    private FavouriteItem favouriteItem;
    private String titlesLeague[] = {"Table", "Fixture", "News"};
    private String titlesTeam[] = {"News", "Fixture", "Table", "Squad"};
    private String[] footballLeagueTitle = {"News", "Fixture", "Table"};
    private String[] footballTeamTitle = {"News", "Fixture", "Squad"};
    private String[] cricketTeamTitle = {"News", "Fixture", "Squad"};

    public ViewPagerAdapterForTeamAndLeagueDetails(FragmentManager fm, FavouriteItem f) {
        super(fm);
//        this.Titles = titles;
//        this.numberOfTabs = 4;
        favouriteItem = f;

        if (f.getFilterType().equalsIgnoreCase(Constants.FILTER_TYPE_LEAGUE)) {
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
            fragment = new MatchListFragment();
            fragment.setArguments(bundle);

        } else if (position == 2) {
            if (favouriteItem.getFilterType().equals(Constants.FILTER_TYPE_LEAGUE)) {
                Bundle b = new Bundle();
                b.putString(Constants.INTENT_KEY_LEAGUE_ID, favouriteItem.getId());
                fragment = new UpCommingFootballMatchTableFargment();
                fragment.setArguments(b);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.INTENT_KEY_TEAM1_ID, favouriteItem.getJsonObject().toString());
                fragment = new UpCommingFootballMatchSqadFragment();
                fragment.setArguments(bundle);
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
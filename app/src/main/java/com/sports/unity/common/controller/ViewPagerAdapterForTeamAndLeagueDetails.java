package com.sports.unity.common.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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

/**
 * Created by Edwin on 15/02/2015.
 */
public class ViewPagerAdapterForTeamAndLeagueDetails extends FragmentStatePagerAdapter {

    private String Titles[];
    private int numberOfTabs;
    private boolean enabled = true;

    private String id = null;
    private String name = null;
    private String type = null;

    String titlesLeague[] = {"Table","Fixture","News"};
    String titlesTeam[] = {"News","Fixture","Table","Squad"};

    //String titles[] = {"Fixture","News","Table","Squad"};

    public ViewPagerAdapterForTeamAndLeagueDetails(FragmentManager fm, String id, String name, String type) {
        super(fm);

        this.id = id;
        this.name = name;
        this.type = type;
//        this.Titles = titles;
//        this.numberOfTabs = 4;

        if(type.equals(Constants.FILTER_TYPE_LEAGUE)){
            this.Titles = titlesLeague;
            this.numberOfTabs = 3;
        } else {
            this.Titles = titlesTeam;
            this.numberOfTabs = 4;
        }

    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            if(type.equals(Constants.FILTER_TYPE_LEAGUE)) {
                MatchListFragment matchesFragment = new MatchListFragment();
                return matchesFragment;

            } else {
                NewsFragment newsFragment = new NewsFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.INTENT_KEY_SEARCH_ON, true);
                bundle.putString("search_keyword", name);
                newsFragment.setArguments(bundle);
                return newsFragment;
            }

        } else if (position == 1) {
                MatchListFragment matchesFragment = new MatchListFragment();
                return matchesFragment;

        } else if (position == 2) {
            if(type.equals(Constants.FILTER_TYPE_LEAGUE)) {
                NewsFragment newsFragment = new NewsFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.INTENT_KEY_SEARCH_ON, true);
                bundle.putString("search_keyword", name);
                newsFragment.setArguments(bundle);
                return newsFragment;

            } else {
                MatchListFragment matchesFragment = new MatchListFragment();
                return matchesFragment;

            }

        } else if (position == 3) {

            MatchListFragment matchesFragment = new MatchListFragment();
            return matchesFragment;

        }
        return null;


//            if (position == 0) {
//                MatchListFragment matchesFragment = new MatchListFragment();
//                return matchesFragment;
//
//            } else if (position == 1) {
//                NewsFragment newsFragment = new NewsFragment();
//                Bundle bundle = new Bundle();
//                bundle.putBoolean(Constants.INTENT_KEY_SEARCH_ON, true);
//                bundle.putString("search_keyword", name);
//                newsFragment.setArguments(bundle);
//                return newsFragment;
//            }else if (position == 2) {
//                MatchListFragment matchesFragment = new MatchListFragment();
//                return matchesFragment;
//            }else if (position == 3) {
//                NewsFragment newsFragment = new NewsFragment();
//                Bundle bundle = new Bundle();
//                bundle.putBoolean(Constants.INTENT_KEY_SEARCH_ON, true);
//                bundle.putString("search_keyword", name);
//                newsFragment.setArguments(bundle);
//                return newsFragment;
//            }
//       return null;
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
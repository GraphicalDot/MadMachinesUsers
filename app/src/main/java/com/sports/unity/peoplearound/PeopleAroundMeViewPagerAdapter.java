package com.sports.unity.peoplearound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.messages.controller.model.Person;
import com.sports.unity.scoredetails.CommentaryFragment;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.UpcommingMatchCommentaryFragment;
import com.sports.unity.scoredetails.cricketdetail.CompletedMatchScoreCardFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketCompletedMatchSummaryFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketLiveMatchSummaryFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketUpcomingMatchScoreCardFragment;
import com.sports.unity.scoredetails.cricketdetail.CricketUpcomingMatchSummaryFragment;
import com.sports.unity.scoredetails.cricketdetail.LiveCricketMatchScoreCardFragment;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeViewPagerAdapter extends FragmentStatePagerAdapter  {




        private String titles[];
        private int numberOfTabs;
        private List<Person> people;



        public PeopleAroundMeViewPagerAdapter(FragmentManager fm, String[] titles, int numberOfTabs,ArrayList<Person> people) {
            super(fm);
            this.titles = titles;
            this.numberOfTabs = numberOfTabs;
            this.people = people;

        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            if (position == 0) {
               fragment = new PeopleAroundMeFragment();
            } else if (position == 1) {
                fragment = new PeopleAroundMeFragment();
            } else {
                fragment = new PeopleAroundMeFragment();
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


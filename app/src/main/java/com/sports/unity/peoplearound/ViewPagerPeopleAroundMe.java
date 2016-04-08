package com.sports.unity.peoplearound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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

/**
 * Created by madmachines on 8/4/16.
 */
public class ViewPagerPeopleAroundMe extends FragmentStatePagerAdapter  {




        private String titles[];
        private int numberOfTabs;



        public ViewPagerPeopleAroundMe(FragmentManager fm, String[] titles, int numberOfTabs) {
            super(fm);
            this.titles = titles;
            this.numberOfTabs = numberOfTabs;

        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            if (position == 0) {

            } else if (position == 1) {

            } else {

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


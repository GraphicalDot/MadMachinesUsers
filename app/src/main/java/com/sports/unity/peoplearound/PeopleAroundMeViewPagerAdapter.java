package com.sports.unity.peoplearound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.messages.controller.model.Person;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeViewPagerAdapter extends FragmentStatePagerAdapter  {




        private String titles[];
        private int numberOfTabs;
        private ArrayList<Person> peopleFriends;
        private ArrayList<Person> peopleSU;
        private ArrayList<Person> peopleNeedHeading ;





    public PeopleAroundMeViewPagerAdapter(FragmentManager fm, String[] titles, int numberOfTabs, ArrayList<Person> peopleFriends, ArrayList<Person> peopleSU, ArrayList<Person> peopleNeedHeading) {
        super(fm);
        this.titles = titles;
        this.numberOfTabs = numberOfTabs;
        this.peopleFriends = peopleFriends;
        this.peopleSU = peopleSU;
        this.peopleNeedHeading = peopleNeedHeading;
    }

    @Override
        public Fragment getItem(int position) {

            Fragment fragment = new PeopleAroundMeFragment();
            Bundle bundle = new Bundle();
            if (position == 0) {
                bundle.putParcelableArrayList(Constants.PARAM_PEOPLES,peopleFriends);
            } else if (position == 1) {
                bundle.putParcelableArrayList(Constants.PARAM_PEOPLES,peopleSU);
            } else {
                bundle.putParcelableArrayList(Constants.PARAM_PEOPLES,peopleNeedHeading);
            }
        fragment.setArguments(bundle);
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


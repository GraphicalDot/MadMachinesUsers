package com.sports.unity.playerprofile.cricket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sports.unity.messages.controller.fragment.ChatFragment;
import com.sports.unity.util.Constants;

import org.json.JSONObject;

/**
 * Created by madmachines on 17/2/16.
 */
public class CricketPlayerProfileAdapter extends FragmentStatePagerAdapter {


    private String titles[];
    private int numberOfTabs;
    private String content = null;

    public CricketPlayerProfileAdapter(FragmentManager fm, String[] titles, int numberOfTabs, JSONObject content) {
        super(fm);
        this.titles = titles;
        this.numberOfTabs = numberOfTabs;
        if (content != null) {
            this.content = content.toString();
        }
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            CricketPlayerBioFragment cricketPlayerBioFragment = new CricketPlayerBioFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            cricketPlayerBioFragment.setArguments(bundle);
            return cricketPlayerBioFragment;
        } else {
            CricketPlayerMachStatFragment cricketPlayerMachStatFragment = new CricketPlayerMachStatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            cricketPlayerMachStatFragment.setArguments(bundle);
            return cricketPlayerMachStatFragment;
        }

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

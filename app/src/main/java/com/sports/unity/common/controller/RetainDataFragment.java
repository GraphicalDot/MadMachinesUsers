package com.sports.unity.common.controller;

import android.support.v4.app.Fragment;

import com.sports.unity.Database.SportsUnityDBHelper;

import java.util.ArrayList;

/**
 * Created by madmachines on 5/10/15.
 */
public class RetainDataFragment extends Fragment {

    private ArrayList<SportsUnityDBHelper.Contacts> contactList = null;

    public ArrayList<SportsUnityDBHelper.Contacts> getContactList() {
        contactList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContactList();
        return contactList;
    }
}

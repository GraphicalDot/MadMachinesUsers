package com.sports.unity.common.controller;

import android.support.v4.app.Fragment;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.messages.controller.model.Contacts;

import java.util.ArrayList;

/**
 * Created by madmachines on 5/10/15.
 */
public class RetainDataFragment extends Fragment {

    private ArrayList<Contacts> contactList = null;

    public ArrayList<Contacts> getContactList() {
        contactList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContactList_AvailableOnly();
        return contactList;
    }
}

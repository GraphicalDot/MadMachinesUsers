package com.sports.unity.common.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sports.unity.R;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;


public class FilterFragment extends Fragment {

    private ListView mlistView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_filter, container, false);

        mlistView = (ListView) rootView.findViewById(R.id.filter_listview);
        Bundle bundle = this.getArguments();
        String filter = bundle.getString(Constants.NAV_FILTER);
        initList(filter);
        return rootView;
    }

    private void initList(String filter) {
        ArrayList<String> savedList = UserUtil.getFavouriteFilters();
        ArrayList<String> teams = new ArrayList<>();
        for (String name : savedList) {
            if (name.contains(filter)) {
                name = name.replace(filter, "");
                teams.add(name);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.custom_simple_list_item_1, teams);
        mlistView.setAdapter(adapter);
    }

}

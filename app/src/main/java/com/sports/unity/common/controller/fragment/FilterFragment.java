package com.sports.unity.common.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class FilterFragment extends Fragment {

    private StickyListHeadersListView mlistView;
    private Bundle bundle;
    FilterAdapter filterAdapter;
    ArrayList<FavouriteItem> favList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mlistView = (StickyListHeadersListView) view.findViewById(R.id.filter_listview);
        bundle = getArguments();
        String filter = bundle.getString(Constants.SPORTS_FILTER_TYPE);
        if (filter.equals(Constants.FILTER_TYPE_TEAM)) {
            favList = new ArrayList<>(FavouriteItemWrapper.getInstance().getAllTeams());
        } else if (filter.equals(Constants.FILTER_TYPE_PLAYER)) {
            favList = new ArrayList<>(FavouriteItemWrapper.getInstance().getAllPlayers());
        } else if (filter.equals(Constants.FILTER_TYPE_LEAGUE)) {
            favList = new ArrayList<>(FavouriteItemWrapper.getInstance().getAllLeagues());
        }

        filterAdapter = new FilterAdapter(getActivity(), favList);
        mlistView.setAdapter(filterAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}

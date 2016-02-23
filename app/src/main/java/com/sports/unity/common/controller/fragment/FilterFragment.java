package com.sports.unity.common.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.controller.SelectSportsActivity;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class FilterFragment extends Fragment implements FilterActivity.OnResultReceivedListener{

    private StickyListHeadersListView mlistView;
    private Bundle bundle;
    FilterAdapter filterAdapter;
    ArrayList<FavouriteItem> favList;
    private String filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FilterActivity) getActivity()).addListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

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
        filter = bundle.getString(Constants.SPORTS_FILTER_TYPE);
        if (filter.equals(Constants.FILTER_TYPE_TEAM)) {
            favList = FavouriteItemWrapper.getInstance(getActivity()).getAllTeams();
        } else if (filter.equals(Constants.FILTER_TYPE_PLAYER)) {
            favList = FavouriteItemWrapper.getInstance(getActivity()).getAllPlayers();
        } else if (filter.equals(Constants.FILTER_TYPE_LEAGUE)) {
            favList = FavouriteItemWrapper.getInstance(getActivity()).getAllLeagues();
        }
        if (favList.size() > 0) {
            filterAdapter = new FilterAdapter(getActivity(), favList);
            mlistView.setAdapter(filterAdapter);
        } else {
            mlistView.setVisibility(View.INVISIBLE);
            showErrorLayout(view);
        }
    }

    /* private void searchNews(String s) {

         Intent newsSearch=new Intent(getActivity(),NewsSearchActivity.class);
         newsSearch.putExtra(Constants.FILTER_SEARCH_EXTRA, s);
         getActivity().startActivity(newsSearch);
     }*/
    private void showErrorLayout(View view) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.error);
        layout.setVisibility(View.VISIBLE);
        TextView headingText = (TextView) layout.findViewById(R.id.oops);
        TextView messageText = (TextView) layout.findViewById(R.id.something_wrong);
        TextView addText = (TextView) layout.findViewById(R.id.addteam);
        if (filter.equals(Constants.FILTER_TYPE_TEAM)) {
            headingText.setText("No Teams Selected :(");
            messageText.setText("Add your favourite teams \n to see more details");
        } else if (filter.equals(Constants.FILTER_TYPE_LEAGUE)) {
            headingText.setText("No Leagues Selected :(");
            messageText.setText("Add your favourite leagues \n to see more details");
        } else if (filter.equals(Constants.FILTER_TYPE_PLAYER)) {
            headingText.setText("No Players Selected :(");
            messageText.setText("Add your favourite players \n to see more details");
        }
        addText.setText("ADD SPORTS");
        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectSportsActivity.class);
                intent.putExtra(Constants.RESULT_REQUIRED, true);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SPORT);
            }
        });
    }

    private void hideErrorLayout(View view) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.error);
        layout.setVisibility(View.INVISIBLE);
        mlistView.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateData() {
        try {
            if (filter.equals(Constants.FILTER_TYPE_TEAM)) {
                favList = FavouriteItemWrapper.getInstance(getActivity()).getAllTeams();
            } else if (filter.equals(Constants.FILTER_TYPE_PLAYER)) {
                favList = FavouriteItemWrapper.getInstance(getActivity()).getAllPlayers();
            } else if (filter.equals(Constants.FILTER_TYPE_LEAGUE)) {
                favList = FavouriteItemWrapper.getInstance(getActivity()).getAllLeagues();
            }
            if (favList.size() > 0) {
                hideErrorLayout(getView());
                filterAdapter = new FilterAdapter(getActivity(), favList);
                mlistView.setAdapter(filterAdapter);
            } else {
                mlistView.setVisibility(View.INVISIBLE);
                showErrorLayout(getView());
            }
        } catch (Exception e) {
        }
    }


}

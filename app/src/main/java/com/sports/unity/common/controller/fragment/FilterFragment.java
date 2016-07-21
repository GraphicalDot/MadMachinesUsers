package com.sports.unity.common.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.controller.AdvancedFilterActivity;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.controller.TeamLeagueDetails;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.FirebaseUtil;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class FilterFragment extends Fragment implements FilterActivity.OnResultReceivedListener {

    private StickyListHeadersListView mlistView;
    private Bundle bundle;
    private FilterAdapter filterAdapter;
    private ArrayList<FavouriteItem> favList;
    private String filter;
    private RelativeLayout cricketLayout;
    private RelativeLayout footballLayout;
    private TextView editCricket;
    private TextView editFootball;
    private LinearLayout emptyLayout;
    private LinearLayout parentEmpty;
    private ImageView sepBottom;

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
        emptyLayout = (LinearLayout) view.findViewById(R.id.error);
        editCricket = (TextView) view.findViewById(R.id.edit_cricket);
        editFootball = (TextView) view.findViewById(R.id.edit_football);
        parentEmpty = (LinearLayout) view.findViewById(R.id.parent_empty);
        cricketLayout = (RelativeLayout) view.findViewById(R.id.cricket_layout);
        footballLayout = (RelativeLayout) view.findViewById(R.id.football_layout);
        sepBottom = (ImageView) view.findViewById(R.id.sep_bottom);
        mlistView.setEmptyView(emptyLayout);
        bundle = getArguments();
        filter = bundle.getString(Constants.SPORTS_FILTER_TYPE);
        if (filter.equals(Constants.FILTER_TYPE_TEAM)) {
            favList = FavouriteItemWrapper.getInstance(getActivity()).getAllTeams();
        } else if (filter.equals(Constants.FILTER_TYPE_PLAYER)) {
            favList = FavouriteItemWrapper.getInstance(getActivity()).getAllPlayers();
        } else if (filter.equals(Constants.FILTER_TYPE_LEAGUE)) {
            cricketLayout.setVisibility(View.GONE);
            favList = FavouriteItemWrapper.getInstance(getActivity()).getAllLeagues();
        }
        if (favList.size() <= 0) {
            showErrorLayout(getView());
            parentEmpty.setVisibility(View.VISIBLE);
            sepBottom.setVisibility(View.VISIBLE);
            if (!filter.equals(Constants.FILTER_TYPE_LEAGUE) && UserUtil.getSportsSelected().contains(Constants.GAME_KEY_CRICKET)) {
                cricketLayout.setVisibility(View.VISIBLE);
            }
            if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
                footballLayout.setVisibility(View.VISIBLE);
            }
        } else {
            initAddLayout();
        }
        filterAdapter = new FilterAdapter(getActivity(), favList);
        mlistView.setAdapter(filterAdapter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavouriteItem f = favList.get(position);
                //FIREBASE INTEGRATION
                {
                    FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(getActivity());
                    Bundle bundle = new Bundle();
                    String name = f.getName();
                    bundle.putString(FirebaseUtil.Param.NAME, FirebaseUtil.trimValue(name));
                    bundle.putString(FirebaseUtil.Param.ID, f.getId());
                    bundle.putString(FirebaseUtil.Param.SPORTS_TYPE, f.getSportsType());
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, f.getFilterType());
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, FirebaseUtil.Event.FILTER_FAV_DETAIL + "_" + f.getSportsType().substring(0, 1) + "_" + f.getFilterType());
                }
                Intent intent = new Intent(getContext(), TeamLeagueDetails.class);
                intent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, f.getJsonObject().toString());
                intent.putExtra(Constants.RESULT_REQUIRED, true);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SPORT);
            }
        });
        editCricket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("max", "edit click out cricket");
                //FIREBASE INTEGRATION
                {
                    FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(getActivity());
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseUtil.Param.SPORTS_TYPE, Constants.SPORTS_TYPE_CRICKET);
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, filter);
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, FirebaseUtil.Event.FILTER_EDIT_CLICK + "_" + "c" + "_" + filter);
                }
                Intent intent = new Intent(new Intent(getActivity(), AdvancedFilterActivity.class));
                intent.putExtra(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_CRICKET);
                intent.putExtra(Constants.SPORTS_FILTER_TYPE, filter);
                intent.putExtra(Constants.RESULT_REQUIRED, true);
                intent.putExtra(Constants.RESULT_SINGLE_USE, true);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SPORT);
            }
        });
        editFootball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIREBASE INTEGRATION
                {
                    FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(getActivity());
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseUtil.Param.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
                    bundle.putString(FirebaseUtil.Param.FILTER_TYPE, filter);
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, FirebaseUtil.Event.FILTER_EDIT_CLICK + "_" + "f" + "_" + filter);
                }
                Intent intent = new Intent(new Intent(getActivity(), AdvancedFilterActivity.class));
                intent.putExtra(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
                intent.putExtra(Constants.SPORTS_FILTER_TYPE, filter);
                intent.putExtra(Constants.RESULT_REQUIRED, true);
                intent.putExtra(Constants.RESULT_SINGLE_USE, true);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SPORT);
            }
        });
    }

    private void initAddLayout() {
        boolean isCricket = false;
        boolean isFootball = false;
        for (FavouriteItem f : favList) {
            if (f.getSportsType().equals(Constants.SPORTS_TYPE_CRICKET)) {
                isCricket = true;
            }
            if (f.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                isFootball = true;
            }
        }

        sepBottom.setVisibility(View.GONE);
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_CRICKET) && !isCricket && !filter.equals(Constants.FILTER_TYPE_LEAGUE)) {
            parentEmpty.setVisibility(View.VISIBLE);
            cricketLayout.setVisibility(View.VISIBLE);
        } else {
            cricketLayout.setVisibility(View.GONE);
        }
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL) && !isFootball) {
            parentEmpty.setVisibility(View.VISIBLE);
            footballLayout.setVisibility(View.VISIBLE);
        } else {
            footballLayout.setVisibility(View.GONE);
        }
        if (isCricket && isFootball) {
            parentEmpty.setVisibility(View.GONE);
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
        if (filter.equals(Constants.FILTER_TYPE_TEAM)) {
            headingText.setText("No Teams Selected");
            messageText.setText("Add your favourite teams \n to see more details");
        } else if (filter.equals(Constants.FILTER_TYPE_LEAGUE)) {

            headingText.setText("No Leagues Selected");
            messageText.setText("Add your favourite leagues \n to see more details");
        } else if (filter.equals(Constants.FILTER_TYPE_PLAYER)) {
            headingText.setText("No Players Selected");
            messageText.setText("Add your favourite players \n to see more details");
        }
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
            filterAdapter = new FilterAdapter(getActivity(), favList);
            mlistView.setAdapter(filterAdapter);
            if (favList.size() <= 0) {
                showErrorLayout(getView());
                sepBottom.setVisibility(View.VISIBLE);
                parentEmpty.setVisibility(View.VISIBLE);
                if (!filter.equals(Constants.FILTER_TYPE_LEAGUE) && UserUtil.getSportsSelected().contains(Constants.GAME_KEY_CRICKET)) {
                    cricketLayout.setVisibility(View.VISIBLE);
                }
                if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
                    footballLayout.setVisibility(View.VISIBLE);
                }
            } else {
                initAddLayout();
            }

        } catch (Exception e) {
        }
    }


}

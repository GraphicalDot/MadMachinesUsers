package com.sports.unity.common.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.AdvancedFilterActivity;
import com.sports.unity.common.model.FavouriteContentHandler;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by Mad on 12/28/2015.
 */
public class FilterByTagFragment extends Fragment implements AdvancedFilterActivity.OnEditFilterListener, FavouriteContentHandler.ListPreparedListener {
    private RecyclerView filterRecyclerView;
    private ArrayList<FavouriteItem> itemDataSet;
    private ArrayList<Boolean> checkBoxState;
    private Bundle bundle;
    private String SPORTS_FILTER_TYPE, SPORTS_TYPE;
    private FavouriteContentHandler favouriteContentHandler;
    private FilterRecycleAdapter itemAdapter;
    ArrayList<FavouriteItem> localList;

    private LinearLayout errorLayout;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        favouriteContentHandler = FavouriteContentHandler.getInstance();
        favouriteContentHandler.addPrepareListener(this);
        SPORTS_FILTER_TYPE = bundle.getString(Constants.SPORTS_FILTER_TYPE);
        SPORTS_TYPE = bundle.getString(Constants.SPORTS_TYPE);
    }

    @Override
    public void onPause() {
        super.onPause();
        favouriteContentHandler.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        favouriteContentHandler.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.filterbytag_fargment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AdvancedFilterActivity) getActivity()).addEditClickListener(this);
        initViews(view);
        setUpRecyclerView(view);
    }

    private void initViews(View view) {
        errorLayout = (LinearLayout) view.findViewById(R.id.error);
        TextView oops = (TextView) errorLayout.findViewById(R.id.oops);
        oops.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());

        TextView something_wrong = (TextView) errorLayout.findViewById(R.id.something_wrong);
        something_wrong.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void setUpRecyclerView(View view) {
        filterRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        filterRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        filterRecyclerView.setLayoutManager(mLayoutManager);
        progressBar.setVisibility(View.VISIBLE);
        if(favouriteContentHandler.isDisplay){
            hideProgress();
            prepareList();
        }
    }

    private void prepareList() {


        if (SPORTS_FILTER_TYPE.equals(Constants.FILTER_TYPE_TEAM)) {

            if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_CRICKET)) {
                itemDataSet = favouriteContentHandler.getFavCricketTeams();

            } else if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                itemDataSet = favouriteContentHandler.getFavFootballTeams();
            }

        } else if (SPORTS_FILTER_TYPE.equals(Constants.FILTER_TYPE_PLAYER)) {

            if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_CRICKET)) {
                itemDataSet = favouriteContentHandler.getFavCricketPlayers();

            } else if (SPORTS_TYPE.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                itemDataSet = favouriteContentHandler.getFavFootballPlayers();

            }

        } else if (SPORTS_FILTER_TYPE.equals(Constants.FILTER_TYPE_LEAGUE)) {
            itemDataSet = favouriteContentHandler.getFavFootballLeagues();

        }
        if(itemDataSet==null){
            hideProgress();
            showErrorLayout();
        }else {
            displayContent();
        }
    }

    private void displayContent() {
        if (!UserUtil.isFilterCompleted() || ((AdvancedFilterActivity) getActivity()).isFromNav) {
            itemAdapter = new FilterRecycleAdapter(getActivity(), itemDataSet, true);
            filterRecyclerView.setAdapter(itemAdapter);
        } else {
            ArrayList<String> favList = new ArrayList<String>();
            ArrayList<String> savedFavlist = UserUtil.getFavouriteFilters();
            for (FavouriteItem f : itemDataSet) {
                if (savedFavlist.contains(f.getName())) {
                    favList.add(f.getName());
                }
            }
            itemAdapter = new FilterRecycleAdapter(getActivity(), favList);
            filterRecyclerView.setAdapter(itemAdapter);

        }
    }

    public synchronized void enableEditMode() {
        if (((AdvancedFilterActivity) getActivity()).isSearchEdit) {

            ArrayList<FavouriteItem> searchList = new ArrayList<FavouriteItem>(itemDataSet);
            localList = new ArrayList<FavouriteItem>();
            for (FavouriteItem f : searchList) {
                boolean b = false;
                String[] split = ((AdvancedFilterActivity) getActivity()).searchString.toLowerCase().split("\\s+");
                for (String s : split) {
                    if (f.getName().toLowerCase().contains(s)) {
                        b = true;
                    }
                }
                if (b) {
                    localList.add(f);
                }
            }
            itemAdapter = new FilterRecycleAdapter(getActivity(), localList, true);
            filterRecyclerView.setAdapter(itemAdapter);
            filterRecyclerView.invalidate();
        } else {

            /*for(FavouriteItem f:localList){
                if(f.isChecked()){
                    //TODO
                    *//*Merge changes if any*//*
                    itemDataSet.add(f);
                }
            }*/
            itemAdapter = new FilterRecycleAdapter(getActivity(), itemDataSet, true);
            filterRecyclerView.setAdapter(itemAdapter);
            filterRecyclerView.invalidate();
        }
    }


    private void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void disableEditMode() {
        prepareList();
        filterRecyclerView.invalidate();
    }

    @Override
    public void onEdit() {
        if (((AdvancedFilterActivity) getActivity()).isEditMode) {
            enableEditMode();
        } else {
            disableEditMode();
        }
    }
    @Override
    public void onListPrepared(Boolean b) {
        if (b) {
            prepareList();
            hideErrorLayout();
            hideProgress();
        } else {
            hideProgress();
            showErrorLayout();
        }
    }
}

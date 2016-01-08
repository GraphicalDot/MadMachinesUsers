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
import java.util.ConcurrentModificationException;

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
    ArrayList<FavouriteItem> searchList;
    ArrayList<String> searchStringList;
    private boolean isEdit;
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
        isFilterCompleted = UserUtil.isFilterCompleted();
        isFromNav = ((AdvancedFilterActivity) getActivity()).isFromNav;
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
        if (favouriteContentHandler.isDisplay) {
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
        if (itemDataSet == null) {
            hideProgress();
            showErrorLayout();
        } else {
            displayContent();
        }
    }

    private boolean isFilterCompleted;
    private boolean isFromNav;

    private void displayContent() {
        hideErrorLayout();
        filterRecyclerView.setVisibility(View.VISIBLE);
        if (!isFilterCompleted || isFromNav) {
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

    private void requestEdit(boolean b) {
        if (b) {
            isEdit = true;
            showProgress();
            favouriteContentHandler.requestFavSearch(SPORTS_FILTER_TYPE, ((AdvancedFilterActivity) getActivity()).searchString);

            Log.d("max", "Requesting Search");
        } else if (!isFilterCompleted || isFromNav) {

            searchList = itemAdapter.getItemDataSet();
            if (searchList.size() > 0) {
                Log.d("max", "Closing Search");
                try {
                    for (FavouriteItem f : searchList) {
                        Log.d("max", "Name is" + f.getName());
                        if (f.isChecked()) {
                            itemDataSet.add(f);
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    Log.d("max", e.toString());
                    filterRecyclerView.setVisibility(View.VISIBLE);
                    hideErrorLayout();
                }
                isEdit = false;
                hideErrorLayout();
                filterRecyclerView.setVisibility(View.VISIBLE);
                itemAdapter = new FilterRecycleAdapter(getActivity(), itemDataSet, true);
                filterRecyclerView.setAdapter(itemAdapter);
                filterRecyclerView.invalidate();
            }
        } else {
            Log.d("hjk", "CLOSING");
            displayContent();
        }
    }

    public void enableEditMode() {
        if ((isEdit && isFromNav) || !isFilterCompleted) {
            searchList = new ArrayList<FavouriteItem>();
            searchList = favouriteContentHandler.getSearchList();
            Log.d("max", "ENABLED"+searchList.size());
            itemAdapter = new FilterRecycleAdapter(getActivity(), searchList, true);
            filterRecyclerView.setVisibility(View.VISIBLE);
            filterRecyclerView.setAdapter(itemAdapter);
            filterRecyclerView.invalidate();
        } else {
            searchStringList = new ArrayList<String>();
            searchStringList = favouriteContentHandler.getSearchStringList();
            itemAdapter = new FilterRecycleAdapter(getActivity(), searchStringList);
            filterRecyclerView.setVisibility(View.VISIBLE);
            filterRecyclerView.setAdapter(itemAdapter);
            filterRecyclerView.invalidate();
        }
    }


    private void showErrorLayout() {
        filterRecyclerView.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        filterRecyclerView.setVisibility(View.VISIBLE);
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
    public void onEdit(boolean b) {
        requestEdit(b);
    }

    @Override
    public void onListPrepared(Boolean b) {
        if (!isEdit) {
            if (b) {
                prepareList();
                hideErrorLayout();
                hideProgress();
            } else {
                hideProgress();
                showErrorLayout();
            }
        } else {

            Log.d("max", "ResultSearch" + b);
            if (b) {
                enableEditMode();
                hideErrorLayout();
                hideProgress();
            } else {
                hideProgress();
                showErrorLayout();
            }
        }
    }
}

package com.sports.unity.scores.controller.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.football.FootballLiveScore;
import com.sports.unity.scores.model.football.FootballLiveScoreResult;

import java.util.ArrayList;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MatchListFragment extends Fragment {

//    private static final String liveScore = "http://52.74.142.219:8080/get_league_fixtures?league_id=1204&date=" + formattedDate;

    private static final String LIST_LISTENER_KEY = "list_listener";
    private static final String LIST_OF_MATCHES_REQUEST_TAG = "list_request_tag";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<FootballLiveScoreResult> matches = new ArrayList<>();

    private ScoresContentListener contentListener = new ScoresContentListener();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(com.sports.unity.R.layout.fragment_match_list, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_scores_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        addResponseListener();
        if( matches.size() == 0 ) {
            Log.i("List of Matches", "Through Resume");

            showProgress(getView());
            requestContent();
        } else {
            //nothing
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        removeResponseListener();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initView(View view){

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_scores);
        mRecyclerView.setHasFixedSize(true);

        MatchListAdapter mAdapter = new MatchListAdapter(matches, getActivity(), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        initErrorLayout(view);
        hideErrorLayout(view);

        initProgress(view);
        hideProgress(view);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.app_theme_blue));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        Log.i("List of Matches", "Swipe Refresh Call");

                        requestContent();
                        mSwipeRefreshLayout.setRefreshing(true);
                    }

                });
            }

        });
    }

    private void renderContent(){
        Log.i("List of Matches", "Render Content");

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private boolean handleContent(String content){
        Log.i("List of Matches", "Handle Content");
        boolean success = false;

        ArrayList<FootballLiveScoreResult> list = null;
        try {
            FootballLiveScore footballLiveScore = new Gson().fromJson(content, FootballLiveScore.class);
            list = (ArrayList<FootballLiveScoreResult>) footballLiveScore.getFootballLiveScoreResult();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if( list != null && list.size() > 0 ){
            matches.clear();
            matches.addAll(list);
            success = true;
        } else {
            //nothing
        }
        return success;
    }

    private void initErrorLayout(View view){
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);

        TextView oops = (TextView)errorLayout.findViewById(R.id.oops);
        oops.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());

        TextView something_wrong = (TextView) errorLayout.findViewById(R.id.something_wrong);
        something_wrong.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());
    }

    private void showErrorLayout(View view){
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout(View view){
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);
    }

    private void addResponseListener(){
        ScoresContentHandler.getInstance().addResponseListener(contentListener, LIST_LISTENER_KEY);
    }

    private void removeResponseListener(){
        ScoresContentHandler.getInstance().removeResponseListener(LIST_LISTENER_KEY);
    }

    private void initProgress(View view){
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void showProgress(View view){
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(View view){
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }

    private void requestContent() {
        Log.i("List of Matches", "Request Content");

        hideErrorLayout(getView());

        ScoresContentHandler.getInstance().requestListOfMatches(LIST_LISTENER_KEY, LIST_OF_MATCHES_REQUEST_TAG);

//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
//        String formattedDate = df.format(c.getTime());
//
//        String liveScore = "http://52.74.142.219:8080/get_league_fixtures?league_id=1204&date=" + formattedDate;
//        String URL_UPCOMING_MATCHES = "http://52.74.142.219:8080/get_football_upcoming_fixtures";
//
    }

    private class ScoresContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if( tag.equals(LIST_OF_MATCHES_REQUEST_TAG) ){
                boolean success = false;
                if( responseCode == 200 ){
                    success = MatchListFragment.this.handleContent(content);
                    if (success) {
                        hideErrorLayout(MatchListFragment.this.getView());
                        MatchListFragment.this.renderContent();
                    } else {
                        Log.i("List of Matches", "Error In Handling Content");
                        showErrorLayout(MatchListFragment.this.getView());
                    }
                } else {
                    Log.i("List of Matches", "Error In Response");
                    showErrorLayout(MatchListFragment.this.getView());
                }

                hideProgress(getView());
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                //nothing
            }
        }
    }

}

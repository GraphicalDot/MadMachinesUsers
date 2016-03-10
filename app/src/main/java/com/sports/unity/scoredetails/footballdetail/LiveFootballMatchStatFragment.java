package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballMatchStatAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballMatchStatDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by madmachines on 23/2/16.
 */
public class LiveFootballMatchStatFragment extends Fragment implements CompletedFootballMatchStatHandler.CompletedFootballMatchContentListener {


    String matchName = "";
    private ProgressBar progressBar;
    private String matchId;
    private RecyclerView rvFootballMatchStat;
    private TextView nocomments;
    private CompleteFootballMatchStatAdapter completeFootballMatchStatAdapter;

    private Map<String,CompleteFootballMatchStatDTO> map = new HashMap<>();
    private int baseWidth;

    private SwipeRefreshLayout swipeRefreshLayout;
    private CompletedFootballMatchStatHandler completedFootballMatchStatHandler;

    public LiveFootballMatchStatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        baseWidth = (int) (metrics.widthPixels * .40);
        Intent i = getActivity().getIntent();
        matchName = i.getStringExtra(Constants.INTENT_KEY_MATCH_NAME);
        matchId = i.getStringExtra(Constants.INTENT_KEY_ID);
        completedFootballMatchStatHandler = CompletedFootballMatchStatHandler.getInstance(context);
        completedFootballMatchStatHandler.addListener(this);
        completedFootballMatchStatHandler.requestCompledFootabllMatchStat(matchId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_completed_football_match_stats, container, false);
        initView(view);


        return view;
    }

    private void initView(View view) {
        try {

            rvFootballMatchStat = (RecyclerView) view.findViewById(R.id.rv_football_match_stat);
            //rvFootballMatchStat.setHasFixedSize(true);
            rvFootballMatchStat.setNestedScrollingEnabled(false);
            rvFootballMatchStat.setLayoutManager(new LinearLayoutManager(getContext()));
            completeFootballMatchStatAdapter = new CompleteFootballMatchStatAdapter(map, getContext());
            rvFootballMatchStat.setAdapter(completeFootballMatchStatAdapter);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sv_swipe_football_match_stat);
            initErrorLayout(view);
            nocomments = (TextView) view.findViewById(R.id.no_comments);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (completedFootballMatchStatHandler != null) {
                        completedFootballMatchStatHandler.requestCompledFootabllMatchStat(matchId);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleContent(String object) {
        {

            try {
                showProgressBar();
                JSONObject jsonObject = new JSONObject(object);
                boolean success = jsonObject.getBoolean("success");
                boolean error = jsonObject.getBoolean("error");

                if (success) {

                    renderDisplay(jsonObject);
                } else {
                    showErrorLayout(getView());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorLayout(getView());
            }
        }
    }

    private void initErrorLayout(View view) {
        try {
            LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        map.clear();
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (!jsonObject.isNull("data")) {
            final JSONArray dataArray = jsonObject.getJSONArray("data");
            final JSONObject teamFirstStatsObject = dataArray.getJSONObject(0);
            final JSONObject teamSecondStatsObject = dataArray.getJSONObject(1);
            final Iterator<String> keysSetItr = teamFirstStatsObject.keys();
            hideProgressBar();
            swipeRefreshLayout.setRefreshing(false);

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            CompleteFootballMatchStatDTO completeFootballMatchStatDTO = null;
                            while (keysSetItr.hasNext()) {
                                String key = keysSetItr.next();
                                try {
                                    if (getLabelValue(key) != null) {
                                        if (!(key.equals("match_id") || key.equals("team"))) {
                                            completeFootballMatchStatDTO = new CompleteFootballMatchStatDTO();
                                            completeFootballMatchStatDTO.setTvLable(getLabelValue(key));
                                            completeFootballMatchStatDTO.setIvLeftStatus(teamFirstStatsObject.getString(key));
                                            completeFootballMatchStatDTO.setIvRightStatus(teamSecondStatsObject.getString(key));
                                            int red = Integer.parseInt(teamFirstStatsObject.getString(key));
                                            int blue = Integer.parseInt(teamSecondStatsObject.getString(key));
                                            completeFootballMatchStatDTO.setLeftGraphValue((baseWidth * red) / (red + blue));
                                            completeFootballMatchStatDTO.setRightGraphValue((baseWidth * blue) / (red + blue));
                                            //Log.i("MatchStatFragment: ", completeFootballMatchStatDTO.toString());
                                          //  list.add(completeFootballMatchStatDTO);
                                            map.put(getLabelValue(key),completeFootballMatchStatDTO);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            completeFootballMatchStatAdapter.notifyDataSetChanged();


                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showErrorLayout(getView());
                        }
                    }
                });
            }

        } else {
            showErrorLayout(getView());
        }

    }

    private String getLabelValue(String tvLable) {
        String lableValue = null;

        switch (tvLable) {
            case "possestiontimetotal":
                lableValue = "POSSESION (%)";
                break;
            case "shotstotal":
                lableValue = "SHOTS";
                break;
            case "shotsongoal":
                lableValue = "SHOTS ON TARGET";
                break;
            case "cornerstotal":
                lableValue = "CORNERS";
                break;
            case "foulstotal":
                lableValue = "FOULS";
                break;
            case "offsidestotal":
                lableValue = "OFFSIDES";
                break;
        }
        return lableValue;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (completedFootballMatchStatHandler != null) {
            completedFootballMatchStatHandler.addListener(this);
        } else {
            completedFootballMatchStatHandler = CompletedFootballMatchStatHandler.getInstance(getContext());
            completedFootballMatchStatHandler.addListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (completedFootballMatchStatHandler != null)
            completedFootballMatchStatHandler = null;

    }
}

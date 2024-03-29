package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballMatchStatAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballMatchStatDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.model.ScoresContentHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;

/**
 * Created by madmachines on 23/2/16.
 */
public class CompletedFootballMatchStatFragment extends BasicVolleyRequestResponseViewHelper {

    private String title;
    private HashMap<String, String> requestParameters;
    private JSONObject response;

    private View contentLayout = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CompleteFootballMatchStatAdapter completeFootballMatchStatAdapter;
    private ArrayList<CompleteFootballMatchStatDTO> dataStatsList = new ArrayList<CompleteFootballMatchStatDTO>();

    public CompletedFootballMatchStatFragment(String title) {
        this.title = title;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_completed_football_match_stats;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "FootballStatsRequestListener";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        MatchStatsComponentListener componentListener = new MatchStatsComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return "MatchStatsRequestTag";
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_FOOTBALL_STATS;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }


    public void setRequestParameters(HashMap<String, String> params) {
        this.requestParameters = params;
    }

    private void initView(View view) {
        try {
            Context context = view.getContext();

            contentLayout = view.findViewById(R.id.content_layout);

            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    requestContent();
                }

            });

            RecyclerView rvFootballMatchStat = (RecyclerView) contentLayout;
            RecyclerView.LayoutManager manager = new android.support.v7.widget.LinearLayoutManager(context);
            rvFootballMatchStat.setLayoutManager(manager);
            completeFootballMatchStatAdapter = new CompleteFootballMatchStatAdapter(dataStatsList, context);
            rvFootballMatchStat.setAdapter(completeFootballMatchStatAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean handleContent(String object) {
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(object);
            success = jsonObject.getBoolean("success");
            if (success) {
                response = jsonObject;
            } else {
                //nothing
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private boolean renderDisplay() {
        boolean success = false;

        dataStatsList.clear();
        if (!response.isNull("data")) {
            try {
                JSONArray dataArray = response.getJSONArray("data");
                if (dataArray.length() != 0) {
                    JSONObject teamSecondStatsObject = null;
                    JSONObject teamFirstStatsObject = null;
                    JSONObject tempObject = dataArray.getJSONObject(0);
                    if (tempObject.getString("team").equalsIgnoreCase("localteam")) {
                        teamFirstStatsObject = dataArray.getJSONObject(0);
                        teamSecondStatsObject = dataArray.getJSONObject(1);
                    } else {
                        teamFirstStatsObject = dataArray.getJSONObject(1);
                        teamSecondStatsObject = dataArray.getJSONObject(0);
                    }
                    String[] keys = {"possesiontime", "shots_total", "shots_ongoal", "corners", "fouls", "offsides"};
                    CompleteFootballMatchStatDTO completeFootballMatchStatDTO = null;

                    for (int index = 0; index < keys.length; index++) {
                        String key = keys[index];
                        try {
                            completeFootballMatchStatDTO = new CompleteFootballMatchStatDTO();
                            completeFootballMatchStatDTO.setTvLable(getLabelValue(key));
                            completeFootballMatchStatDTO.setIvLeftStatus(teamFirstStatsObject.getString(key));
                            completeFootballMatchStatDTO.setIvRightStatus(teamSecondStatsObject.getString(key));
                            int red = Integer.parseInt(teamFirstStatsObject.getString(key));
                            int blue = Integer.parseInt(teamSecondStatsObject.getString(key));
                            float total = red + blue;
                            if (total == 0) {
                                completeFootballMatchStatDTO.setLeftGraphValue(1);
                                completeFootballMatchStatDTO.setRightGraphValue(1);
                            } else {
                                completeFootballMatchStatDTO.setLeftGraphValue( 1 - (red / total) );
                                completeFootballMatchStatDTO.setRightGraphValue( 1 - (blue / total) );
                            }
                            dataStatsList.add(completeFootballMatchStatDTO);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    completeFootballMatchStatAdapter.notifyDataSetChanged();
                    success = true;
                } else {
                    //nothing
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //nothing
        }
        return success;
    }

    private String getLabelValue(String tvLable) {
        String lableValue = null;

        switch (tvLable) {
            case "possesiontime":
                lableValue = "POSSESION (%)";
                break;
            case "shots_total":
                lableValue = "SHOTS";
                break;
            case "shots_ongoal":
                lableValue = "SHOTS ON TARGET";
                break;
            case "corners":
                lableValue = "CORNERS";
                break;
            case "fouls":
                lableValue = "FOULS";
                break;
            case "offsides":
                lableValue = "OFFSIDES";
                break;
        }
        return lableValue;
    }

    public class MatchStatsComponentListener extends CustomComponentListener {

        public MatchStatsComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        protected boolean isContentLayoutAvailable() {
            return dataStatsList.size() > 0;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = CompletedFootballMatchStatFragment.this.handleContent(content);
            return success;
        }


        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if( success ){
                //nothing
            } else {
                showErrorLayout();
            }
        }

    }

}

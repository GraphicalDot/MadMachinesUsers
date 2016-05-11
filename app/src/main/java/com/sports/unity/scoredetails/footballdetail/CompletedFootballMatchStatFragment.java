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

    private int baseWidth;
    private String title;
    private HashMap<String,String> requestParameters;
    private JSONObject response;

    private RecyclerView rvFootballMatchStat;
    private CompleteFootballMatchStatAdapter completeFootballMatchStatAdapter;
    private Map<String,CompleteFootballMatchStatDTO> map = new HashMap<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    public CompletedFootballMatchStatFragment(String title, int baseWidth) {
        this.title = title;
        this.baseWidth = baseWidth;
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
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);

        MatchStatsComponentListener  componentListener = new MatchStatsComponentListener( getRequestTag(), progressBar, errorLayout);
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


    public void setRequestParameters(HashMap<String,String> params ) {
        this.requestParameters = params;
    }

    private void initView(View view) {
        try {
            Context context = view.getContext();

            rvFootballMatchStat = (RecyclerView) view.findViewById(R.id.rv_football_match_stat);
            rvFootballMatchStat.setNestedScrollingEnabled(false);
            rvFootballMatchStat.setLayoutManager(new LinearLayoutManager(context));
            completeFootballMatchStatAdapter = new CompleteFootballMatchStatAdapter(map, context);
            rvFootballMatchStat.setAdapter(completeFootballMatchStatAdapter);

            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sv_swipe_football_match_stat);
            swipeRefreshLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    requestContent();
                }

            });
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

        map.clear();
        if ( ! response.isNull("data") ) {
            try {
                JSONArray dataArray = response.getJSONArray("data");
                if( dataArray.length() != 0 ){
                    JSONObject teamSecondStatsObject = dataArray.getJSONObject(0);
                    JSONObject teamFirstStatsObject = dataArray.getJSONObject(1);

                    Iterator<String> keysSetItr = teamFirstStatsObject.keys();
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
                                    int total = red + blue;
                                    if (total == 0) {
                                        completeFootballMatchStatDTO.setLeftGraphValue(0);
                                        completeFootballMatchStatDTO.setRightGraphValue(0);
                                    } else {
                                        completeFootballMatchStatDTO.setLeftGraphValue((baseWidth * red) / (red + blue));
                                        completeFootballMatchStatDTO.setRightGraphValue((baseWidth * blue) / (red + blue));
                                    }
                                    map.put(getLabelValue(key), completeFootballMatchStatDTO);
                                }
                            }
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

        public MatchStatsComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        protected void showErrorLayout() {
            if( swipeRefreshLayout.getVisibility() == View.VISIBLE ) {
                //nothing
            } else {
                super.showErrorLayout();
            }
        }

        @Override
        protected void showProgress() {
            if( swipeRefreshLayout.getVisibility() == View.VISIBLE ) {
                //nothing
            } else {
                super.showProgress();
            }
        }

        @Override
        protected void hideProgress() {
            super.hideProgress();

            if( swipeRefreshLayout != null ){
                swipeRefreshLayout.setRefreshing(false);
            }
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
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                showErrorLayout();
            }
        }

    }

}
